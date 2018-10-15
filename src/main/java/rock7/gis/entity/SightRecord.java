package rock7.gis.entity;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by mikehoughton on 14/10/2018.
 */
@Entity
public class SightRecord {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO) long id;
  @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")

  private DateTime date;
  private Integer number;

  public SightRecord() {
  }

  public SightRecord(DateTime date, Integer number) {
    this.date = date;
    this.number = number;
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
