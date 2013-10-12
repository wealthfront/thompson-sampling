package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BatchedThompsonSamplingTest {
  @Test
  public void testUpdate() {
    BatchedThompsonSampling bandit = new BatchedThompsonSampling(2);
    bandit.update(Lists.newArrayList(new ObservedArmPerformance(1, 2), new ObservedArmPerformance(3, 4)));
    assertEquals(Lists.newArrayList(new ObservedArmPerformance(1, 2), new ObservedArmPerformance(3, 4)), bandit.performances);
    bandit.update(Lists.newArrayList(new ObservedArmPerformance(1, 2), new ObservedArmPerformance(3, 4)));
    assertEquals(Lists.newArrayList(new ObservedArmPerformance(2, 4), new ObservedArmPerformance(6, 8)), bandit.performances);
    try {
      bandit.update(Lists.newArrayList(new ObservedArmPerformance(1, 2), new ObservedArmPerformance(3, 4), new ObservedArmPerformance(5, 6)));
      fail("Expecting IllegalArgumentException");
    } catch (IllegalArgumentException e) { }
  }

  @Test
  public void testGetBanditStatistics() {
    //RandomEngine engine = new MersenneTwister(51); // 106 iterations
    //RandomEngine engine = new MersenneTwister(52); // 20 iterations
    //RandomEngine engine = new MersenneTwister(53); // 12 iterations
    //RandomEngine engine = new MersenneTwister(54); // 153 iterations
    //RandomEngine engine = new MersenneTwister(55); // 10 iterations
    //RandomEngine engine = new MersenneTwister(56); // 44 iterations
    //RandomEngine engine = new MersenneTwister(57); // 189 iterations
    //RandomEngine engine = new MersenneTwister(58); // 76 iterations
    //RandomEngine engine = new MersenneTwister(59); // 37 iterations
    RandomEngine engine = new MersenneTwister(60); // 45 iterations
    BernouliArm loser = new BernouliArm(0.01, engine);
    BernouliArm winner = new BernouliArm(0.015, engine);
    List<BernouliArm> arms = Lists.newArrayList(loser, winner);
    BatchedThompsonSampling batchedBandit = new BatchedThompsonSampling(2);
    batchedBandit.setRandomEngine(engine);
    BanditStatistics currentStatistics = new BanditStatistics(Lists.newArrayList(
        new Double(0.5), new Double(0.5)), Optional.<Integer>absent());
    Optional<Integer> victoriousArm = currentStatistics.getVictoriousArm();
    int iteration = 0;
    while (!victoriousArm.isPresent()) {
      List<ObservedArmPerformance> batchPerformances = Lists.newArrayList(
          new ObservedArmPerformance(0, 0),
          new ObservedArmPerformance(0, 0));
      System.out.println(String.format("Iteration %d:", iteration));
      System.out.println(String.format("\tLosing arm (success, failure):  %s", batchedBandit.getPerformances().get(0)));
      System.out.println(String.format("\tWinning arm (success, failure): %s", batchedBandit.getPerformances().get(1)));
      System.out.println(String.format("\tLosing arm weight:  %.2f", currentStatistics.getArmWeights().get(0)));
      System.out.println(String.format("\tWinning arm weight: %.2f", currentStatistics.getArmWeights().get(1)));
      for (int i = 0; i < 100; i++) {
        int arm = currentStatistics.pickArm(engine);
        if (arms.get(arm).draw()) {
          batchPerformances.get(arm).addSuccess();
        } else {
          batchPerformances.get(arm).addFailure();
        }
      }
      batchedBandit.update(batchPerformances);
      currentStatistics = batchedBandit.getBanditStatistics();
      victoriousArm = currentStatistics.getVictoriousArm();
      iteration++;
    }
    int winningArm = currentStatistics.getVictoriousArm().get();
    ObservedArmPerformance loserPerf = batchedBandit.getPerformances().get(0);
    ObservedArmPerformance winnerPerf = batchedBandit.getPerformances().get(1);
    double conversionRate = (loserPerf.getSuccesses() + winnerPerf.getSuccesses()) * 1.0 /
        (loserPerf.getFailures() + loserPerf.getSuccesses() + winnerPerf.getFailures() + winnerPerf.getSuccesses());
    double regret = (0.015 - conversionRate) / 0.015;
    System.out.println(String.format("Final iteration: %d", iteration));
    System.out.println(String.format("\tWinning arm: %d", winningArm));
    System.out.println(String.format("\tLosing arm (success, failure):  %s", loserPerf));
    System.out.println(String.format("\tWinning arm (success, failure): %s", winnerPerf));
    System.out.println(String.format("\tLosing arm weight:  %.2f", currentStatistics.getArmWeights().get(0)));
    System.out.println(String.format("\tWinning arm weight: %.2f", currentStatistics.getArmWeights().get(1)));
    System.out.println(String.format("\tConversion rate: %.4f", conversionRate));
    System.out.println(String.format("\tRegret:          %.4f", regret));
    assertEquals(1, winningArm);
  }
}
