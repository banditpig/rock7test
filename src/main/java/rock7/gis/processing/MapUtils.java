package rock7.gis.processing;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Component;
import rock7.gis.entity.Position;
import rock7.gis.entity.Race;
import rock7.gis.entity.Team;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mikehoughton on 12/10/2018.
 */
@Component
public class MapUtils {

  private static final int TIME_DELTA = 30;
//  1.17 times the square root of your height of eye (in feet)
// = Distance to the horizon in nautical miles
  //say 12 feet  high - gives around 7.5 km
  private static final double HORIZON_DISTANCE = 7.5;

  public Double distance(Position p, Position p0) {
    double x = p.getLatitude().doubleValue() - p0.getLatitude().doubleValue();
    double y = (p.getLongitude().doubleValue() - p0.getLongitude().doubleValue()) * Math.cos(p0.getLatitude().doubleValue());

    return 110.25 * Math.sqrt(x * x + y * y);

  }

  public List<Position> positionsForThatDay(List<Position> positions, DateTime thatDay) {
    //ie all observations on that day
    return positions.stream()
        .filter(p -> sameDay(p.getGpsAt(), thatDay))
        .collect(Collectors.toList());
  }

  public List<DateTime> uniqueDays(List<Position> positions) {
    //unique days - ignore time
    Set<DateTime> uniqueDays = new TreeSet<>(); //natural sort order
    positions.stream().forEach(p -> uniqueDays.add(p.getGpsAt().withTimeAtStartOfDay()));
    return new ArrayList<>(uniqueDays);

  }


  public boolean sameDay(DateTime t1, DateTime t2) {

    Interval theDay = new Interval(t1.withTimeAtStartOfDay(), t1.plusDays(1).withTimeAtStartOfDay());

    return theDay.contains(t2);

  }


  private Map<DateTime, Integer> normalisePerDay(Map<DateTime, Integer> dayMap){

    Map<DateTime, Integer> perDayMap = new TreeMap<>();

    for(DateTime d : dayMap.keySet()){
      DateTime perDayKey = d.withTimeAtStartOfDay();
      if (perDayMap.containsKey(perDayKey)){
        Integer newCount = dayMap.get(d) + perDayMap.get(perDayKey);
        perDayMap.put(perDayKey, newCount);
      }else{
        perDayMap.put(perDayKey, dayMap.get(d));
      }
    }
    return perDayMap;
  }
  private Map<DateTime, Integer>  mergeMaps(Map<DateTime, Integer> map1, Map<DateTime, Integer> map2){

    Map<DateTime, Integer>  map3 = new TreeMap<>(map1);
    map2.forEach((k, v) -> map3.merge(k, v, Integer::sum));
    return map3;
  }
  public Map<DateTime, Integer> processPosLists(List<Position> posList1, List<Position> posList2 ){

    // Expects that lists are in ascending gps datetime order
    Map<DateTime, Integer> dayCountMap = new TreeMap<>();

    //Unique days in list1 and 2
    List<DateTime> uniqueDays1 = uniqueDays(posList1);
    List<DateTime> uniqueDays2 = uniqueDays(posList2);

    List<DateTime> uniqueDays = uniqueDays2;
    if (uniqueDays1.size() > uniqueDays2.size()){
      uniqueDays = uniqueDays1;
    }

    for(DateTime day : uniqueDays){
      List<Position> list1Day = positionsForThatDay(posList1, day);
      List<Position> list2Day = positionsForThatDay(posList2, day);

      //sighting will check for at most one match on each day
      if  (sighting(list1Day, list2Day)){
        dayCountMap.put(day, 1);

      }else{
        dayCountMap.put(day, 0);
      }
    }

    return dayCountMap;
  }


  private boolean sighting(List<Position> posList1, List<Position> posList2){
    List<Position> srcList;
    List<Position> trgList;
    if (posList1.size() < posList2.size() ){
      srcList = posList2;
      trgList = posList1;
    }else{
      srcList = posList1;
      trgList = posList2;
    }

    for (int i =0; i<srcList.size(); i++){

      Position srcPos = srcList.get(i);


      for(int j = 0; j < trgList.size(); j++){
        Position trgPos = trgList.get(j);
        if (srcPos.getGpsAt().equals(trgPos.getGpsAt())){
         if (positionsCloseTogether(srcPos, trgPos)){
           return true;
          }
        }

      }

    }

    return false;
  }

  public boolean positionsCloseTogether(Position p1, Position p2){
    //expect them to be on same day.

//    Interval timeWindow = new Interval(t1.minusSeconds(TIME_DELTA), t1.plusSeconds(TIME_DELTA));
//    if (! timeWindow.contains(t2)) return false;
    //temporally local...
    //check distance
    double distance = distance(p1.getLatitude().doubleValue(),
        p1.getLongitude().doubleValue(), p2.getLatitude().doubleValue(), p2.getLongitude().doubleValue());

    if (distance <= HORIZON_DISTANCE) return true;
    return false;
  }
  public Double totalDistanceTravelled(List<Position> positions) {
    double total = 0;
    int ix = 0;
    while (ix < positions.size() - 1) {
      total = total + distance(positions.get(ix), positions.get(ix + 1));
      ix++;
    }
    return total;
  }


  public Double distance(double lat, double lng, double lat0, double lng0) {
    //http://jonisalonen.com/2014/computing-distance-between-coordinates-can-be-simple-and-fast/
    double x = lat - lat0;
    double y = (lng - lng0) * Math.cos(lat0);

    return 110.25 * Math.sqrt(x * x + y * y);

  }

  public   Map<String,Map<DateTime, Integer>> teamSiteings(List<Team> teams){

    Map<String, Map<DateTime, Integer>  > teamSiteings = new TreeMap<>();

    //
    for(int ix = 0; ix < teams.size(); ix++){
      List<Position> ps1 = teams.remove(ix).postitionByTime();

      for(Team team : teams ){
        Map<DateTime, Integer> dayCountMap = processPosLists(ps1, team.postitionByTime());

        //TODO should be for BOTH team name here. So include both or none in the final output.
        //TODO if A can see B then B can see A
        if (teamSiteings.containsKey(team.getName())){
          Map<DateTime, Integer> prevMap = teamSiteings.get(team.getName());
          Map<DateTime, Integer> newMap = mergeMaps(prevMap, dayCountMap );
          teamSiteings.put(team.getName(), newMap);
        }else{
          teamSiteings.put(team.getName(), dayCountMap);
        }
      }

    }
    return teamSiteings;



  }
  public static void main(String[] args) throws IOException {
    JsonIO jsonIO = new JsonIO();
    MapUtils mapUtils = new MapUtils();

    Race race = jsonIO.fromJSON(jsonIO.fromFile("positions.json"));

    DateTime t3 = new DateTime("2017-12-06T08:00:00.000Z");
    DateTime t2 = new DateTime("2017-11-19T09:00:00.000Z");
    DateTime t1 = new DateTime("2017-11-19T11:45:00.000Z");

    System.out.println(mapUtils.sameDay(t2, t1));
    System.out.println(mapUtils.sameDay(t2, t3));

    // 0 0 = 5
    // 20 20 = 35
    //1 19 = 14
    // 38 43 = 6
    // 43 38 =6
    // 19 123 = 13




    List<Team> teams = race.getTeams();
    List<Position> ps1 = teams.get(56).postitionByTime();
    List<Position> ps2= teams.get(123).postitionByTime();
    Map<String, Map<DateTime, Integer>  > m = mapUtils.teamSiteings(teams.subList(0,15));
    int total = 0;

    Map<DateTime, Integer> start = new TreeMap<>();
    for (String name: m.keySet()){
      Map<DateTime, Integer> next =  m.get(name);
      start =  mapUtils.mergeMaps(next,start);


    }

    for(DateTime d : start.keySet()){

      System.out.println(d + " " + start.get(d));
      total += start.get(d);
    }
//    for(String name : m.keySet()){
//      System.out.println(name );
//      Map<DateTime, Integer> data = m.get(name);
//      for(DateTime d : data.keySet()){
//        System.out.println(d + " " + data.get(d));
//        total += data.get(d);
//      }
//
//    }
//

    System.out.println("-> " + total);
//
//    System.out.println(ps1.size());
//    System.out.println(ps2.size());
//    Map<DateTime, Integer> m = mapUtils.processPosLists(ps1,ps2);
//
//    int total = 0;
//    for(DateTime d : m.keySet()){
//      System.out.println(d + " - "  + m.get(d));
//      total += m.get(d);
//    }
//    System.out.println("-> " + total);
//
//
//    System.out.println("-============");
//    Map<DateTime, Integer> normalised = mapUtils.normalisePerDay(m);
//     total = 0;
//    for(DateTime d : normalised.keySet()){
//      System.out.println(d + " - "  + normalised.get(d));
//      total += normalised.get(d);
//    }
//    System.out.println("-> " + total);


//    System.out.println(mapUtils.totalDistanceTravelled(ps1));
//    System.out.println(mapUtils.totalDistanceTravelled(ps2));
//
//
//    Map<String, Double> nameDistanceMap = new TreeMap<>();
//    teams.stream().forEach(t -> {
//          double current =  mapUtils.totalDistanceTravelled(t.postitionByTime());
//      nameDistanceMap.put(t.getName(), current );
//
//
//        }
//    );
//
//    double min = Double.MAX_VALUE;
//    double max = 0;
//
//    String minName = "";
//    String maxName = "";
//    double total = 0;
//    for(String name: nameDistanceMap.keySet()){
//      total += nameDistanceMap.get(name);
//
//      double current = nameDistanceMap.get(name);
//      if (current > max ) {
//        max = current;
//        maxName = name;
//      }
//      if (current < min ){
//        min = current;
//        minName = name;
//      }
//
//      System.out.println(name + ":" + String.format("%.0f", current ));
//    }
//    System.out.println("Total distance:" + String.format("%.0f", total));
//    System.out.println("Average distance:" + String.format("%.0f", total/nameDistanceMap.keySet().size()));
//    System.out.println("Max distance:" + maxName + ":" + String.format("%.0f", max) );
//    System.out.println("Min distance:" + minName + ":" + String.format("%.0f", min) );

//    teams.get(10).postitionByTime().forEach(p -> System.out.println(p.getGpsAt() + " " + p.getLatitude() + " " + p.getLongitude()));
//    System.out.println();
  }
}
