package rock7.gis.processing;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Component;
import rock7.gis.entity.Position;
import rock7.gis.entity.Race;
import rock7.gis.entity.Team;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by mikehoughton on 12/10/2018.
 */
@Component
public class MapUtils {

  private static final int TIME_DELTA = 1;
  private static final double HORIZON_DISTANCE = 1.5;

  public Double distance(Position p, Position p0) {
    double x = p.getLatitude().doubleValue() - p0.getLatitude().doubleValue();
    double y = (p.getLongitude().doubleValue() - p0.getLongitude().doubleValue()) * Math.cos(p0.getLatitude().doubleValue());

    return 110.25 * Math.sqrt(x * x + y * y);

  }

  public List<Position> forThatDay(List<Position> positions, DateTime thatDay) {
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

  public int processPosLists(List<Position> posList1, List<Position> posList2 ){

    // Expects that lists are in ascending gps datetime order
    AtomicInteger totalMatches = new AtomicInteger(0);
    Map<DateTime, Integer> dayCountMap = new TreeMap<>();
    posList1.stream().forEach(p -> {
      totalMatches.addAndGet(mapPosOverPosList(p, posList2, dayCountMap));
    } );
    System.out.println(dayCountMap);
    dayCountMap.keySet().forEach(k -> System.out.println(k + " - "  + dayCountMap.get(k)));
    return totalMatches.intValue();
  }
  private int mapPosOverPosList(Position fixedPos, List<Position> posList, Map<DateTime, Integer> dayCountMap){

    /*
    'Stamp' fixedPos over each in poslist so long as they are both on the same day.
    If they are on the same day then check for spatial proximity.
     */
    int matchCount = 0;
    boolean tooLate = false;
    int ix = 0;
    while (! tooLate && (ix < posList.size())){
      Position currentPos = posList.get(ix);

      if( !sameDay(fixedPos.getGpsAt(), currentPos.getGpsAt())){
        tooLate = true;
      }else{
        //maybe nearby?
        if (positionsCloseTogether(fixedPos, currentPos)){
          matchCount++;
        }
      }
      ix++;
    }
//    final int mCount = matchCount;
    dayCountMap.put(fixedPos.getGpsAt(), matchCount);
//    dayCountMap.compute(fixedPos.getGpsAt().withTimeAtStartOfDay(), (k, v) -> (v == null) ? 0 : v + mCount );
    return matchCount;
  }
  public boolean positionsCloseTogether(Position p1, Position p2){
    //expect them to be on same day.
    // lat long are local too
    DateTime t1 = p1.getGpsAt();
    DateTime t2 = p2.getGpsAt();


//    Interval timeWindow = new Interval(t1.minusSeconds(TIME_DELTA), t1.plusSeconds(TIME_DELTA));
//    if (! timeWindow.contains(t2)) return false;
    if (! t1.equals(t2)) return false;
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

//  public int teamSiteings(List<Team> teams, ){
//
//    List<Position> ps1 = teams.remove(0).postitionByTime();
//    teams.stream().forEach(t -> {
//      processPosLists(ps1, t.postitionByTime());
//
//    });
//
//
//
//
//  }
  public static void main(String[] args) throws IOException {
    JsonIO jsonIO = new JsonIO();
    MapUtils mapUtils = new MapUtils();

    Race race = jsonIO.fromJSON(jsonIO.fromFile("positions.json"));

    DateTime t3 = new DateTime("2017-12-06T08:00:00.000Z");
    DateTime t2 = new DateTime("2017-11-19T09:00:00.000Z");
    DateTime t1 = new DateTime("2017-11-19T11:45:00.000Z");

    System.out.println(mapUtils.sameDay(t2, t1));
    System.out.println(mapUtils.sameDay(t2, t3));

    List<Team> teams = race.getTeams();
    List<Position> ps1 = teams.get(100).postitionByTime();
    List<Position> ps2= teams.get(0).postitionByTime();

    System.out.println(ps1.size());
    System.out.println(ps2.size());
    int m = mapUtils.processPosLists(ps1,ps2);
    System.out.println(m);

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
