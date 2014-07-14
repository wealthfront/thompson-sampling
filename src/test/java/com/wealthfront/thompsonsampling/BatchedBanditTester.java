package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.RandomEngine;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.List;

import static com.google.common.primitives.Doubles.max;

public class BatchedBanditTester {
  private final BatchedBandit bandit;
  private final RandomEngine randomEngine;
  private int iteration;
  private int winningArm;
  private double cumulativeRegret;

  public BatchedBanditTester(BatchedBandit bandit, RandomEngine engine) {
    this(bandit, engine, Lists.newArrayList(new BernouliArm(0.01, engine), new BernouliArm(0.015, engine)));
  }

  public BatchedBanditTester(BatchedBandit bandit, RandomEngine engine, List<BernouliArm> arms) {
    this.bandit = bandit;
    this.randomEngine = engine;
    List<Double> armWeights = Lists.newArrayList();
    for (int i = 0; i < arms.size(); i++) {
      armWeights.add(new Double(1.0 / arms.size()));
    }
    BanditStatistics currentStatistics = new BanditStatistics(armWeights, Optional.<Integer>absent());
    Optional<Integer> victoriousArm = currentStatistics.getVictoriousArm();
    iteration = 0;
    BanditPerformance performance = new BanditPerformance(arms.size());
    while (!victoriousArm.isPresent()) {
      List<ObservedArmPerformance> batchPerformances = Lists.newArrayList();
      for (int i = 0; i < arms.size(); i++) {
        batchPerformances.add(new ObservedArmPerformance(0, 0));
      }
      for (int i = 0; i < 100; i++) {
        int arm = currentStatistics.pickArm(engine);
        if (arms.get(arm).draw()) {
          batchPerformances.get(arm).addSuccess();
        } else {
          batchPerformances.get(arm).addFailure();
        }
      }
      performance.update(batchPerformances);
      currentStatistics = bandit.getBanditStatistics(performance);
      victoriousArm = currentStatistics.getVictoriousArm();
      iteration++;
    }
    winningArm = currentStatistics.getVictoriousArm().get();
    List<Double> trueConversions = Lists.newArrayList();
    double trueWinner = -1.0;
    for (int i = 0; i < arms.size(); i++) {
      trueWinner = max(trueWinner, arms.get(i).getConversionRate());
      trueConversions.add(arms.get(i).getConversionRate());
    }
    cumulativeRegret = performance.cumulativeRegret(trueWinner, trueConversions);
  }

  public int getIterations() {
    return iteration;
  }

  public int getWinningArm() {
    return winningArm;
  }

  public double getCumulativeRegret() {
    return cumulativeRegret;
  }
}
