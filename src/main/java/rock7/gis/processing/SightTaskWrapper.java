package rock7.gis.processing;

import org.joda.time.DateTime;

import java.util.Map;

/**
 * Created by mikehoughton on 15/10/2018.
 */
public class SightTaskWrapper {
  private String teamName;
  private Map<DateTime, Integer> dayCountMap;

  public SightTaskWrapper(String teamName, Map<DateTime, Integer> dayCountMap) {
    this.teamName = teamName;
    this.dayCountMap = dayCountMap;
  }

  public String getTeamName() {
    return teamName;
  }

  public Map<DateTime, Integer> getDayCountMap() {
    return dayCountMap;
  }
}
