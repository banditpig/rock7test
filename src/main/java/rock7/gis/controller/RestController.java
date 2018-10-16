package rock7.gis.controller;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import rock7.gis.entity.Position;
import rock7.gis.entity.Race;
import rock7.gis.entity.Sighting;
import rock7.gis.entity.StatsWrapper;
import rock7.gis.processing.MapUtils;
import rock7.gis.repos.PositionRepository;
import rock7.gis.repos.RaceRepository;
import rock7.gis.repos.SightingRepo;
import rock7.gis.repos.TeamRepository;

import java.util.*;

@Controller
@RequestMapping(path="/rock7")
public class RestController {


  @Autowired
  private RaceRepository raceRepository;
  @Autowired
  private PositionRepository positionRepository;
  @Autowired
  private TeamRepository teamRepository;

  @Autowired
  private SightingRepo sightingRepository;

  @Autowired
  private MapUtils mapUtils;


  @GetMapping(path = "/stats")
  public @ResponseBody
  StatsWrapper stats() {
    return calcStats();
  }


  @GetMapping(path = "/all")
  public @ResponseBody
  Iterable<Position> getAllPosition() {
    return positionRepository.findAll();
  }

  @GetMapping(path = "/teams")
  public @ResponseBody
  Iterable<String> teams() {

    List<String> names = new ArrayList<>();
    Optional<Race> raceOptional = raceRepository.findById("arc2017");
    raceOptional.ifPresent(r -> r.getTeams().stream().forEach(t -> names.add(t.getName() +":" + t.getSerial())));

    return names;
  }



  @GetMapping(path = "/positions/{teamSerial}")
  public @ResponseBody Iterable<Position> positions(@PathVariable("teamSerial") Integer teamSerial){

    //will bypass getting Race as in the test data there's only one race
    List<Position> positionList = new ArrayList<>();
    teamRepository.findById(teamSerial).ifPresent(t -> positionList.addAll(t.postitionByTime()));

    return positionList;

  }
  @GetMapping(path = "/sightings/generate")
  public @ResponseBody Iterable<Sighting> generateSightings() {
    teamRepository.findAll();

     Map<String, Map<DateTime, Integer>> sightData = mapUtils.teamSiteings(Lists.newArrayList(teamRepository.findAll()));
     List<Sighting> sightingList = new ArrayList<>();
     for (String name: sightData.keySet()){
       Map<DateTime, Integer> sights = sightData.get(name);

       for (DateTime dt : sights.keySet()){
         Sighting sighting = new Sighting(name, dt,sights.get(dt));
         sightingList.add(sighting);
       }

     }

     sightingRepository.saveAll(sightingList);
     return sightingRepository.findAll();
  }
  private StatsWrapper calcStats(){

    Map<Double, String> nameDistanceMap = new TreeMap<>();

    teamRepository.findAll().forEach(t -> {
          double current =  mapUtils.totalDistanceTravelled(t.postitionByTime());
          nameDistanceMap.put(current, mapUtils.makeKey(t)  );
        }
    );

    double minDist = Double.MAX_VALUE;
    double maxDist = 0;

    String minName = "";
    String maxName = "";
    double total = 0;
    for(Double d: nameDistanceMap.keySet()){
      total += d;

      if (d > maxDist ) {
        maxDist = d;
        maxName = nameDistanceMap.get(d);
      }
      if (d < minDist ){
        minDist = d;
        minName = nameDistanceMap.get(d);;
      }
    }


   return new StatsWrapper()
        .withAvgDist(total/nameDistanceMap.keySet().size())
        .withNameDist(nameDistanceMap)
        .withTotalDist(total)
        .withMaxDist(maxDist)
        .withMinDist(minDist)
        .withMinName(minName)
        .withMaxName(maxName);
  }

}
