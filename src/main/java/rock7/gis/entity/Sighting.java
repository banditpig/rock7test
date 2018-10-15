package rock7.gis.entity;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by mikehoughton on 13/10/2018.
 */
@Entity
public final class Sighting {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO) long id;

  //The team doing the sighting. There's probably enough info in
  // mapUtils.teamSiteings to get the team being sighted.
  private String teamName;

  @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  private DateTime date;
  private Integer number;



  public Sighting(){}

  public Sighting(String teamName, DateTime date, Integer number) {
    this.teamName = teamName;
    this.date = date;
    this.number = number;


  }

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }



  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }
}
