package rock7.gis.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
@Entity
public final class Race {

  @Id
  private String raceUrl;
  @OneToMany(
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  private List<Team> teams;


  public String getRaceUrl() {
    return raceUrl;
  }

  public void setRaceUrl(String raceUrl) {
    this.raceUrl = raceUrl;
  }

  public List<Team> getTeams() {
    return teams;
  }

  public void setTeams(List<Team> teams) {
    this.teams = teams;
  }

  @Override
  public String toString() {
    return "Race{" + "teams=" + teams + ", raceUrl='" + raceUrl + '\'' + '}';
  }
}
