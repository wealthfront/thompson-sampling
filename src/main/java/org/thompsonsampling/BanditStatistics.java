package org.thompsonsampling;

import com.google.common.base.Optional;

import java.util.List;

public class BanditStatistics {
  private final List<Double> armWeights;
  private final Optional<Integer> victoriousArm;

  public List<Double> getArmWeights() {
    return armWeights;
  }

  public Optional<Integer> getVictoriousArm() {
    return victoriousArm;
  }

  public BanditStatistics(List<Double> armWeights, Optional<Integer> victoriousArm) {
    this.armWeights = armWeights;
    this.victoriousArm = victoriousArm;
  }
}
