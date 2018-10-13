package rock7.gis.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Entity
public final class Team {

  private Integer marker;

  private String name;
  @Id
  private Integer serial;
  @OneToMany(
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  private List<Position> positions;

  public List<Position> sortedPositions(Comparator<Position> comparator) {
    List<Position> copyPos = new ArrayList<>(positions);
    Collections.sort(copyPos, comparator);
    return copyPos;
  }

  public List<Position> postitionByTime() {

    Comparator<Position> gpsAtComparator = new Comparator<Position>() {
      @Override
      public int compare(Position o1, Position o2) {
        return o1.getGpsAt().compareTo(o2.getGpsAt());
      }
    };
    return sortedPositions(gpsAtComparator);
  }

  public Integer getMarker() {
    return marker;
  }

  public void setMarker(Integer marker) {
    this.marker = marker;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getSerial() {
    return serial;
  }

  public void setSerial(Integer serial) {
    this.serial = serial;
  }

  public List<Position> getPositions() {
    return positions;
  }

  public void setPositions(List<Position> positions) {
    this.positions = positions;
  }

  @Override
  public String toString() {
    return "Team{" + "marker=" + marker + ", name='" + name + '\'' + ", serial=" + serial
        + ", positions=" + positions + '}';
  }
}
