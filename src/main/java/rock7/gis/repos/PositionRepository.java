package rock7.gis.repos;

import org.springframework.data.repository.CrudRepository;
import rock7.gis.entity.Position;

/**
 * Created by mikehoughton on 12/10/2018.
 */
public interface PositionRepository  extends CrudRepository<Position, Long> {
}
