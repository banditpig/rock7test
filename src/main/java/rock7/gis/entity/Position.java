package rock7.gis.entity;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public final class Position {

  @Id
  private Long id;

  private Boolean alert;
  private Long altitude;
  private String type;
  private Float dtfKm;
  private Float dtfNm;
  @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  private DateTime gpsAt;
  @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  private DateTime txAt;
  private Float sogKmph;
  private Float sogKnots;

  private Integer battery;
  private Integer cog;

  private BigDecimal longitude;
  private BigDecimal latitude;
  private Long gpsAtMillis;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Boolean getAlert() {
    return alert;
  }

  public void setAlert(Boolean alert) {
    this.alert = alert;
  }

  public Long getAltitude() {
    return altitude;
  }

  public void setAltitude(Long altitude) {
    this.altitude = altitude;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Float getDtfKm() {
    return dtfKm;
  }

  public void setDtfKm(Float dtfKm) {
    this.dtfKm = dtfKm;
  }

  public Float getDtfNm() {
    return dtfNm;
  }

  public void setDtfNm(Float dtfNm) {
    this.dtfNm = dtfNm;
  }

  public DateTime getGpsAt() {
    return gpsAt;
  }

  public void setGpsAt(DateTime gpsAt) {
    this.gpsAt = gpsAt;
  }

  public DateTime getTxAt() {
    return txAt;
  }

  public void setTxAt(DateTime txAt) {
    this.txAt = txAt;
  }

  public Float getSogKmph() {
    return sogKmph;
  }

  public void setSogKmph(Float sogKmph) {
    this.sogKmph = sogKmph;
  }

  public Float getSogKnots() {
    return sogKnots;
  }

  public void setSogKnots(Float sogKnots) {
    this.sogKnots = sogKnots;
  }

  public Integer getBattery() {
    return battery;
  }

  public void setBattery(Integer battery) {
    this.battery = battery;
  }

  public Integer getCog() {
    return cog;
  }

  public void setCog(Integer cog) {
    this.cog = cog;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }

  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
  }

  public BigDecimal getLatitude() {
    return latitude;
  }

  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  public Long getGpsAtMillis() {
    return gpsAtMillis;
  }

  public void setGpsAtMillis(Long gpsAtMillis) {
    this.gpsAtMillis = gpsAtMillis;
  }

  @Override
  public String toString() {
    return "Position{" + "id=" + id + ", alert=" + alert + ", altitude=" + altitude + ", type='"
        + type + '\'' + ", dtfKm=" + dtfKm + ", dtfNm=" + dtfNm + ", gpsAt=" + gpsAt + ", txAt="
        + txAt + ", sogKmph=" + sogKmph + ", sogKnots=" + sogKnots + ", battery=" + battery
        + ", cog=" + cog + ", longitude=" + longitude + ", latitude=" + latitude + ", gpsAtMillis="
        + gpsAtMillis + '}';
  }
}
