package rock7.gis.rw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rock7.gis.entity.Position;
import rock7.gis.entity.Race;
import rock7.gis.entity.Team;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class JsonIO {

  private final ClassLoader classLoader;
  private final ObjectMapper mapper;



  public String toJSON(Race race) throws JsonProcessingException {
    return mapper.writeValueAsString(race);

  }

  public JsonIO() {
    classLoader = getClass().getClassLoader();
    mapper = new ObjectMapper();
    mapper.registerModule(new JodaModule());
  }

  public Race fromJSON(String json) throws IOException {

    Race race = mapper.readValue(json, Race.class);
    return race;
  }

  public String fromFile(String name) throws IOException {
    Path path = new File(classLoader.getResource(name).getFile()).toPath();
    return new String(Files.readAllBytes(path));//Paths.get(fileAndPath)));
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
    teams.get(10).postitionByTime().forEach(p -> System.out.println(p.getGpsAt() + " " + p.getLatitude() + " " + p.getLongitude()));
    System.out.println();
    System.out.println(mapUtils.totalDistanceTravelled(teams.get(10).getPositions()));
    System.out.println(mapUtils.distance(14.27853, -54.67493, 14.31397, -55.23366));

    // dh.uniqueDays(teams.get(10).getPositions()).forEach(d -> System.out.println(d));
    System.out.println();
    DateTime day0 = new DateTime("2017-11-19T00:00:00.000Z");
    System.out.println(mapUtils.forThatDay(teams.get(10).postitionByTime(), day0).size());

  }
}
