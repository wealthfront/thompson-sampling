package com.wealthfront.thompsonsampling;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class BanditPerformance {

  private final List<ObservedArmPerformance> performances;

  public BanditPerformance(List<ObservedArmPerformance> performances) {
    this.performances = performances;
  }

  public List<ObservedArmPerformance> getPerformances() {
    return performances;
  }

  public double getCumulativeRegret() {
    return performances.stream()
        .map(performance -> {
          long samples = performance.getFailures() + performance.getSuccesses();
          return samples * (getBestArmPerformance() - performance.getExpectedConversionRate());
        })
        .reduce(0.0, Double::sum);
  }

  public double getBestArmPerformance() {
    return performances.stream()
        .max(Comparator.comparing(ObservedArmPerformance::getExpectedConversionRate))
        .orElse(new ObservedArmPerformance("", 0, 0))
        .getExpectedConversionRate();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BanditPerformance that = (BanditPerformance) o;
    return Objects.equals(performances, that.performances);
  }

  @Override
  public int hashCode() {
    return Objects.hash(performances);
  }

}
