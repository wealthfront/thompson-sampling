package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.RandomEngine;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.List;

public class BatchedBanditTester {
  private final BatchedBandit bandit;
  private final RandomEngine randomEngine;
  private int iteration;
  private int winningArm;

  public BatchedBanditTester(BatchedBandit bandit, RandomEngine engine) {
    this.bandit = bandit;
    this.randomEngine = engine;
    BernouliArm loser = new BernouliArm(0.01, engine);
    BernouliArm winner = new BernouliArm(0.015, engine);
    List<BernouliArm> arms = Lists.newArrayList(loser, winner);
    BanditStatistics currentStatistics = new BanditStatistics(Lists.newArrayList(
        new Double(0.5), new Double(0.5)), Optional.<Integer>absent());
    Optional<Integer> victoriousArm = currentStatistics.getVictoriousArm();
    iteration = 0;
    while (!victoriousArm.isPresent()) {
      List<ObservedArmPerformance> batchPerformances = Lists.newArrayList(
          new ObservedArmPerformance(0, 0),
          new ObservedArmPerformance(0, 0));
      for (int i = 0; i < 100; i++) {
        int arm = currentStatistics.pickArm(engine);
        if (arms.get(arm).draw()) {
          batchPerformances.get(arm).addSuccess();
        } else {
          batchPerformances.get(arm).addFailure();
        }
      }
      bandit.update(batchPerformances);
      currentStatistics = bandit.getBanditStatistics();
      victoriousArm = currentStatistics.getVictoriousArm();
      iteration++;
    }
    winningArm = currentStatistics.getVictoriousArm().get();
  }

  public int getIterations() {
    return iteration;
  }

  public int getWinningArm() {
    return winningArm;
  }
}
