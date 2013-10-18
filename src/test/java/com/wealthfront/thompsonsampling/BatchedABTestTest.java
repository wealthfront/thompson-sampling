package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BatchedABTestTest {
  @Test
  public void testCorrectArmChosen() {
    int correct = 0;
    for (int i = 0; i<= 10000; i++) {
      RandomEngine engine = new MersenneTwister(i);
      BatchedABTest batchedBandit = new BatchedABTest(2);
      batchedBandit.setRandomEngine(engine);
      BatchedBanditTester tester = new BatchedBanditTester(batchedBandit, engine);
      if (i % 100 == 0) {
        System.out.println("Batches complete " + i);
      }
      correct += tester.getWinningArm();
    }
    assertTrue(correct > 9500);
  }

  @Test
  public void testChiSquareComputation() {
    BatchedABTest batchedABTest = new BatchedABTest(Lists.newArrayList(new ObservedArmPerformance(100L, 0L),
        new ObservedArmPerformance(0L, 100L)));
    batchedABTest.setRequiresMinSamples(false);
    assertEquals(new Integer(0), batchedABTest.getBanditStatistics().getVictoriousArm().get());
    batchedABTest = new BatchedABTest(Lists.newArrayList(new ObservedArmPerformance(0L, 100L),
        new ObservedArmPerformance(100L, 0L)));
    batchedABTest.setRequiresMinSamples(false);
    assertEquals(new Integer(1), batchedABTest.getBanditStatistics().getVictoriousArm().get());
    batchedABTest = new BatchedABTest(Lists.newArrayList(new ObservedArmPerformance(5L, 5L),
        new ObservedArmPerformance(5L, 5L)));
    batchedABTest.setRequiresMinSamples(false);
    assertFalse(batchedABTest.getBanditStatistics().getVictoriousArm().isPresent());
  }
}
