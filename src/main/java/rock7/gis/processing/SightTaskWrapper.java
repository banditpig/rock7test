package rock7.gis.processing;

import org.joda.time.DateTime;

import java.util.Map;

/**
 * Created by mikehoughton on 15/10/2018.
 */
public class SightTaskWrapper {
  private String teamOneName;
  private String teamTwoName;
  private Map<DateTime, Integer> dayCountMap;

  public SightTaskWrapper(String teamOneName, String teamTwoName, Map<DateTime, Integer> dayCountMap) {
    this.teamOneName = teamOneName;
    this.teamTwoName = teamTwoName;
    this.dayCountMap = dayCountMap;
  }

  public String getTeamOneName() {
    return teamOneName;
  }

  public String getTeamTwoName() {
    return teamTwoName;
  }

  public Map<DateTime, Integer> getDayCountMap() {
    return dayCountMap;
  }
}
