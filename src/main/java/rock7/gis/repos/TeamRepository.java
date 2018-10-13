package rock7.gis.repos;

import org.springframework.data.repository.CrudRepository;
import rock7.gis.entity.Position;
import rock7.gis.entity.Team;

/**
 * Created by mikehoughton on 13/10/2018.
 */
public interface TeamRepository  extends CrudRepository<Team, Integer> {
}
