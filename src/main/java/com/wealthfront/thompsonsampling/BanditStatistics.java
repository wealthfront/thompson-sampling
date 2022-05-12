package com.wealthfront.thompsonsampling;

import java.util.Map;
import java.util.Optional;

public class BanditStatistics {

  private final Map<String, Double> weightsByVariant;
  private final Optional<String> victoriousVariant;

  public BanditStatistics(
      Map<String, Double> armWeightsByVariant,
      Optional<String> victoriousVariant) {
    this.weightsByVariant = armWeightsByVariant;
    this.victoriousVariant = victoriousVariant;
  }

  public Map<String, Double> getWeightsByVariant() {
    return weightsByVariant;
  }

  public Optional<String> getVictoriousVariant() {
    return victoriousVariant;
  }

}
