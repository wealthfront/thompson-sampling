package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;


import static org.junit.Assert.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

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
  @Ignore
  public void testCorrectArmChosen() {
    int correct = 0;
    for (int i = 0; i< 10000; i++) {
      RandomEngine engine = new MersenneTwister(i);
      BatchedThompsonSampling batchedBandit = new BatchedThompsonSampling(2);
      batchedBandit.setRandomEngine(engine);
      BatchedBanditTester tester = new BatchedBanditTester(batchedBandit, engine);
      //System.out.println(tester.getIterations() + ", " + batchedBandit.cumulativeRegret(0.015));
      if (i % 100 == 0) {
        System.out.println(i);
      }
      correct += tester.getWinningArm();
    }
    System.out.println(correct);
    assertTrue(correct > 9900);
  }

  @Test
  public void testPerformance() {
    int maxBanditIterations = 0;
    double maxBanditRegret = 0.0;
    for (int i = 51; i<= 60; i++) {
      RandomEngine engine = new MersenneTwister(i);
      BatchedThompsonSampling batchedBandit = new BatchedThompsonSampling(2);
      batchedBandit.setRandomEngine(engine);
      BatchedBanditTester tester = new BatchedBanditTester(batchedBandit, engine);
      double regret = batchedBandit.cumulativeRegret(0.015);
      //System.out.println(tester.getIterations() + ", " + batchedBandit.cumulativeRegret(0.015));
      maxBanditIterations = max(maxBanditIterations, tester.getIterations());
      maxBanditRegret = max(maxBanditRegret, regret);
      assertEquals(1, tester.getWinningArm());
    }
    int minAbIterations = Integer.MAX_VALUE;
    double minAbRegret = 0.0;
    for (int i = 51; i<= 60; i++) {
      RandomEngine engine = new MersenneTwister(i);
      BatchedABTest batchedBandit = new BatchedABTest(2);
      batchedBandit.setRandomEngine(engine);
      BatchedBanditTester tester = new BatchedBanditTester(batchedBandit, engine);
      double regret = batchedBandit.cumulativeRegret(0.015);
      //System.out.println(tester.getIterations() + ", " + batchedBandit.cumulativeRegret(0.015));
      minAbIterations = min(minAbIterations, tester.getIterations());
      minAbRegret = max(minAbRegret, regret);
      assertEquals(1, tester.getWinningArm());
    }
    System.out.println("Min A/B regret: " + minAbRegret);
    System.out.println("Max Bandit regret: " + maxBanditRegret);
    System.out.println("Min A/B # batches (batch size = 100 samples): " + minAbIterations);
    System.out.println("Max Bandit # batches (batch size = 100 samples): " + maxBanditIterations);
  }
}
