package rock7.gis.processing;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Component;
import rock7.gis.entity.Position;
import rock7.gis.entity.Race;
import rock7.gis.entity.Team;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Component
public class MapUtils {

  private static final  ExecutorService executorService = Executors.newFixedThreadPool(8);

  private static final CompletionService<SightTaskWrapper> taskCompletionService = new ExecutorCompletionService<SightTaskWrapper>(executorService);

  //  1.17 times the square root of your height of eye in feet
  // = Distance to the horizon in nautical miles.
  // Say 12 feet  high - gives around 7.5 km
  private static final double HORIZON_DISTANCE = 7.5;


  public Double totalDistanceTravelled(List<Position> positions) {
    double total = 0;
    int ix = 0;
    while (ix < positions.size() - 1) {
      total = total + distance(positions.get(ix), positions.get(ix + 1));
      ix++;
    }
    return total;
  }

  public  Map<String,Map<DateTime, Integer>> teamSiteings(List<Team> teams){

    Map<String, Map<DateTime, Integer>  > teamSiteings = new TreeMap<>();
    List<Callable<SightTaskWrapper> > allSightTasks = new ArrayList<>();

    for(int ix = 0; ix < teams.size(); ix++){
      Team teamOne = teams.remove(ix);
      List<Position> ps1 = teamOne.postitionByTime();

      for(Team team : teams ){
        allSightTasks.add(sightTask(makeKey(teamOne), makeKey(team), ps1, team.postitionByTime() ));
      }

    }
    //we've made all the tasks. So now submit them.
    for (Callable<SightTaskWrapper> callable : allSightTasks) {
      taskCompletionService.submit(callable);
    }
    for (int i = 0; i < allSightTasks.size(); i++) {
      try {

        Future<SightTaskWrapper> taskWrapper= taskCompletionService.take();

        Map<DateTime, Integer> dayCountMap = taskWrapper.get().getDayCountMap();
        String teamOneName = taskWrapper.get().getTeamOneName();
        String teamTwoName = taskWrapper.get().getTeamTwoName();

        //From here we could get more sophisticated.
        //Eg. have both teams in the db so we can see
        //how many times X spotted Y, and when. Or who was seen the most
        //... teamOneName + " " + teamTwoName

        if (teamSiteings.containsKey(teamOneName)){
          Map<DateTime, Integer> prevMap = teamSiteings.get(teamOneName);
          Map<DateTime, Integer> newMap = mergeMaps(prevMap, dayCountMap );
          teamSiteings.put(teamOneName, newMap);
        }else{
          teamSiteings.put(teamOneName, dayCountMap);
        }

        if (teamSiteings.containsKey(teamTwoName)){
          Map<DateTime, Integer> prevMap = teamSiteings.get(teamTwoName);
          Map<DateTime, Integer> newMap = mergeMaps(prevMap, dayCountMap );
          teamSiteings.put(teamTwoName, newMap);
        }else{
          teamSiteings.put(teamTwoName, dayCountMap);
        }


      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }

    }
    executorService.shutdown();
    return teamSiteings;

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

  private boolean positionsCloseTogether(Position p1, Position p2){
    //expect them to be on same day.

    //check distance
    double distance = distance(p1.getLatitude().doubleValue(),
        p1.getLongitude().doubleValue(), p2.getLatitude().doubleValue(), p2.getLongitude().doubleValue());

    if (distance <= HORIZON_DISTANCE) return true;
    return false;
  }
  private String makeKey(Team team){
    //name is not unique. (Ellen...)
    return team.getName() + " - " + team.getSerial();
  }
  private Callable< SightTaskWrapper > sightTask(String teamOneName, String teamTwoName, List<Position> ps1,List<Position> ps2 ){
    return new Callable<SightTaskWrapper>() {
      @Override
      public SightTaskWrapper call() throws Exception {
        return processPosLists(teamOneName, teamTwoName, ps1, ps2);
      }
    };
  }
  private SightTaskWrapper processPosLists(String teamOneName, String teamTwoName, List<Position> posList1, List<Position> posList2 ){

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

    return new SightTaskWrapper(teamOneName,teamTwoName, dayCountMap);
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



  private Double distance(double lat, double lng, double lat0, double lng0) {
    //http://jonisalonen.com/2014/computing-distance-between-coordinates-can-be-simple-and-fast/
    double x = lat - lat0;
    double y = (lng - lng0) * Math.cos(lat0);

    return 110.25 * Math.sqrt(x * x + y * y);

  }

  private Map<DateTime, Integer>  mergeMaps(Map<DateTime, Integer> map1, Map<DateTime, Integer> map2){

    Map<DateTime, Integer>  map3 = new TreeMap<>(map1);
    map2.forEach((k, v) -> map3.merge(k, v, Integer::sum));
    return map3;
  }


  private Double distance(Position p, Position p0) {
    /*
    Ignore spherical geometry.
     */
    double x = p.getLatitude().doubleValue() - p0.getLatitude().doubleValue();
    double y = (p.getLongitude().doubleValue() - p0.getLongitude().doubleValue()) * Math.cos(p0.getLatitude().doubleValue());

    return 110.25 * Math.sqrt(x * x + y * y);

  }

  private List<Position> positionsForThatDay(List<Position> positions, DateTime thatDay) {
    //ie all observations on that day
    return positions.stream()
        .filter(p -> sameDay(p.getGpsAt(), thatDay))
        .collect(Collectors.toList());
  }

  private List<DateTime> uniqueDays(List<Position> positions) {
    //unique days - ignore time
    Set<DateTime> uniqueDays = new TreeSet<>(); //natural sort order
    positions.stream().forEach(p -> uniqueDays.add(p.getGpsAt().withTimeAtStartOfDay()));
    return new ArrayList<>(uniqueDays);

  }

  private boolean sameDay(DateTime t1, DateTime t2) {

    Interval theDay = new Interval(t1.withTimeAtStartOfDay(), t1.plusDays(1).withTimeAtStartOfDay());
    return theDay.contains(t2);
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


    List<Team> teams = race.getTeams();


    Map<String, Map<DateTime, Integer>  > m = mapUtils.teamSiteings(teams);
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


    System.out.println("-> " + total);

  }
}
