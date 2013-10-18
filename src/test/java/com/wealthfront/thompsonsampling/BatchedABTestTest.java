package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BatchedABTestTest {
  @Test
  public void testCorrectArmChosen() {
    for (int i = 51; i<= 60; i++) {
      RandomEngine engine = new MersenneTwister(i);
      BatchedABTest batchedBandit = new BatchedABTest(2);
      batchedBandit.setRandomEngine(engine);
      BatchedBanditTester tester = new BatchedBanditTester(batchedBandit, engine);
      System.out.println(tester.getIterations() + ", " + batchedBandit.cumulativeRegret(0.015));
      assertEquals(1, tester.getWinningArm());
    }
  }
}
