package rock7.gis.processing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.stereotype.Component;
import rock7.gis.entity.Race;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    return new String(Files.readAllBytes(path));
  }

}
