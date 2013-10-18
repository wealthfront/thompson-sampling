package com.wealthfront.thompsonsampling;

import com.google.common.collect.Lists;

import java.util.List;

public abstract class BaseBatchedBandit implements BatchedBandit {
  protected final List<ObservedArmPerformance> performances;

  protected BaseBatchedBandit(List<ObservedArmPerformance> performances) {
    this.performances = performances;
  }

  protected BaseBatchedBandit(int numberOfArms) {
    this(Lists.<ObservedArmPerformance>newArrayListWithCapacity(numberOfArms));
    for (int i = 0; i < numberOfArms; i++) {
      performances.add(new ObservedArmPerformance(0, 0));
    }
  }

  @Override
  public List<ObservedArmPerformance> getPerformances() {
    return performances;
  }

  @Override
  public void update(List<ObservedArmPerformance> newPerformances) {
    if (newPerformances == null || newPerformances.size() != performances.size()) {
      throw new IllegalArgumentException(String.format("Wrong number of arms given: expected %d.",
          performances.size()));
    }
    for (int i = 0; i < newPerformances.size(); i++) {
      ObservedArmPerformance newPerf = newPerformances.get(i);
      performances.set(i, performances.get(i).add(newPerf));
    }
  }

  @Override
  public double cumulativeRegret(double bestArmPerformance, List<Double> allPerformances) {
    int arms = allPerformances.size();
    double cumulativeRegret = 0.0;
    for (int j = 0; j < arms; j++) {
      long samples = performances.get(j).getFailures() + performances.get(j).getSuccesses();
      double armLoss = samples * (bestArmPerformance - allPerformances.get(j));
      cumulativeRegret += armLoss;
    }
    return cumulativeRegret;
  }
}
