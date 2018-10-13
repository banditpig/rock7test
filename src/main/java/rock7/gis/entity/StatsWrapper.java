package rock7.gis.entity;

import rock7.gis.controller.RestController;

import java.util.List;

/**
 * Created by mikehoughton on 13/10/2018.
 */
public class StatsWrapper {
  private double minDist;
  private double maxDist;
  private double avgDist;
  private double totalDist;
  private String minName;
  private String maxName;
  private List<String> nameDist;

  public StatsWrapper withMinDist(double minDist) {
    this.minDist = minDist;
    return this;
  }

  public StatsWrapper withMaxDist(double maxDist) {
    this.maxDist = maxDist;
    return this;
  }

  public StatsWrapper withAvgDist(double avgDist) {
    this.avgDist = avgDist;
    return this;
  }

  public StatsWrapper withTotalDist(double totalDist) {
    this.totalDist = totalDist;
    return this;
  }

  public StatsWrapper withMinName(String minName) {
    this.minName = minName;
    return this;
  }

  public StatsWrapper withMaxName(String maxName) {
    this.maxName = maxName;
    return this;
  }

  public StatsWrapper withNameDist(List<String> nameDist) {
    this.nameDist = nameDist;
    return this;
  }

  public double getMinDist() {
    return minDist;
  }

  public void setMinDist(double minDist) {
    this.minDist = minDist;
  }

  public double getMaxDist() {
    return maxDist;
  }

  public double getAvgDist() {
    return avgDist;
  }

  public void setAvgDist(double avgDist) {
    this.avgDist = avgDist;
  }

  public double getTotalDist() {
    return totalDist;
  }

  public void setTotalDist(double totalDist) {
    this.totalDist = totalDist;
  }

  public String getMinName() {
    return minName;
  }

  public void setMinName(String minName) {
    this.minName = minName;
  }

  public String getMaxName() {
    return maxName;
  }

  public void setMaxName(String maxName) {
    this.maxName = maxName;
  }

  public List<String> getNameDist() {
    return nameDist;
  }

  public void setNameDist(List<String> nameDist) {
    this.nameDist = nameDist;
  }
}
