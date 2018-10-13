package rock7.gis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import rock7.gis.entity.Position;
import rock7.gis.entity.Race;
import rock7.gis.entity.StatsWrapper;
import rock7.gis.repos.PositionRepository;
import rock7.gis.repos.RaceRepository;
import rock7.gis.repos.TeamRepository;
import rock7.gis.rw.MapUtils;

import java.util.*;

/**
 * Created by mikehoughton on 12/10/2018.
 */
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

  private StatsWrapper calcStats(){

    Map<String, Double> nameDistanceMap = new TreeMap<>();

    teamRepository.findAll().forEach(t -> {
          double current =  mapUtils.totalDistanceTravelled(t.postitionByTime());
          nameDistanceMap.put(t.getName(), current );
        }
    );

    double minDist = Double.MAX_VALUE;
    double maxDist = 0;

    String minName = "";
    String maxName = "";
    double total = 0;
    List<String> nameDist = new ArrayList<>();
    for(String name: nameDistanceMap.keySet()){
      total += nameDistanceMap.get(name);

      double current = nameDistanceMap.get(name);
      if (current > maxDist ) {
        maxDist = current;
        maxName = name;
      }
      if (current < minDist ){
        minDist = current;
        minName = name;
      }
      nameDist.add(name + ":" + String.format("%.0f", current ));
    }


   return new StatsWrapper()
        .withAvgDist(total/nameDistanceMap.keySet().size())
        .withNameDist(nameDist)
        .withTotalDist(total)
        .withMaxDist(maxDist)
        .withMinDist(minDist)
        .withMinName(minName)
        .withMaxName(maxName);
  }

}