package com.wealthfront.thompsonsampling;

import java.util.Map;
import java.util.Optional;

public class WeightUpdate {

  private final Map<String, Double> weightByVariantName;
  private final Optional<String> maybeWinningVariant;
  private final Map<String, ObservedArmPerformance> performancesByVariantName;

  public WeightUpdate(
      Map<String, Double> weightByVariantName,
      Optional<String> maybeWinningVariant,
      Map<String, ObservedArmPerformance> performancesByVariantName) {
    this.weightByVariantName = weightByVariantName;
    this.maybeWinningVariant = maybeWinningVariant;
    this.performancesByVariantName = performancesByVariantName;
  }

  public Map<String, Double> getWeightByVariantName() {
    return weightByVariantName;
  }

  public Optional<String> getMaybeWinningVariant() {
    return maybeWinningVariant;
  }

  public Map<String, ObservedArmPerformance> getPerformancesByVariantName() {
    return performancesByVariantName;
  }

}
