package rock7.gis.repos;

import org.springframework.data.repository.CrudRepository;
import rock7.gis.entity.Race;
import rock7.gis.entity.Sighting;

/**
 * Created by mikehoughton on 14/10/2018.
 */
public interface SightingRepo extends CrudRepository<Sighting, String> {
}
