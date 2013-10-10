package org.thompsonsampling;

import com.google.common.collect.Lists;

import java.util.List;

public class BatchedThompsonSampling implements BatchedBandit {
  final List<ArmPerformance> performances;

  public BatchedThompsonSampling(List<ArmPerformance> performances) {
    this.performances = performances;
  }

  public BatchedThompsonSampling(int numberOfArms) {
    this(Lists.<ArmPerformance>newArrayListWithCapacity(numberOfArms));
    for (int i = 0; i < numberOfArms; i++) {
      performances.add(new ArmPerformance(0, 0));
    }
  }

  @Override
  public void update(List<ArmPerformance> performances) {
    if (performances == null || performances.size() != this.performances.size()) {
      throw new IllegalArgumentException(String.format("Wrong number of arms given: expected %d.",
          this.performances.size()));
    }
    for (int i = 0; i < performances.size(); i++) {
      this.performances.set(i, this.performances.get(i).add(performances.get(i)));
    }
  }

  @Override
  public List<Double> getArmWeights() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
