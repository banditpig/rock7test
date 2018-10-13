package rock7.gis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rock7.gis.repos.PositionRepository;
import rock7.gis.entity.Race;
import rock7.gis.repos.RaceRepository;
import rock7.gis.rw.JsonIO;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by mikehoughton on 12/10/2018.
 */
@SpringBootApplication
public class MainApplication {

  @Autowired
  private RaceRepository raceRepository;
  @Autowired
  private PositionRepository positionRepository;

  @Autowired
  private JsonIO jsonIO;

  @PostConstruct
  public void init() throws IOException {
    Race race = jsonIO.fromJSON(jsonIO.fromFile("positions.json"));
    raceRepository.save(race);

//    positionRepository.findAll().forEach(p -> System.out.println(p));
  }
  public static void main(String[] args) {
    SpringApplication.run(MainApplication.class, args);
  }
}
