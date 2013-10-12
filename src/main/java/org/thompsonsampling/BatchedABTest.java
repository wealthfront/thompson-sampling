package org.thompsonsampling;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class BatchedABTest extends BaseBatchedBandit {
  private int numberOfDraws = 10000;
  private RandomEngine randomEngine = new MersenneTwister(new Date());
  private double confidenceLevel = 0.95;
  private double experimentValueQuitLevel = 0.01;

  public BatchedABTest(List<ObservedArmPerformance> performances) {
    super(performances);
  }

  public BatchedABTest(int numberOfArms) {
    super(numberOfArms);
  }

  @Override
  public BanditStatistics getBanditStatistics() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
