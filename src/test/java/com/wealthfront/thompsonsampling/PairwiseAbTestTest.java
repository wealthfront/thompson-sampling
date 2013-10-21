package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class PairwiseAbTestTest {
  @Test
  public void testCorrectness() {
    List<Double> weights = Lists.newArrayList(0.04, 0.05, 0.045, 0.03, 0.02, 0.035);
    final RandomEngine engine = new MersenneTwister(-1);
    List<BernouliArm> armWeights = FluentIterable.from(weights).transform(new Function<Double, BernouliArm>() {
      @Override
      public BernouliArm apply(Double aDouble) {
        return new BernouliArm(aDouble, engine);
      }
    }).toList();
    BatchedBanditTester tester = new BatchedBanditTester(new PairwiseAbTest(6), engine, armWeights);
    assertEquals(1, tester.getWinningArm());
    weights = Lists.newArrayList(0.04, 0.02, 0.045, 0.03, 0.05, 0.035);
    armWeights = FluentIterable.from(weights).transform(new Function<Double, BernouliArm>() {
      @Override
      public BernouliArm apply(Double aDouble) {
        return new BernouliArm(aDouble, engine);
      }
    }).toList();
    tester = new BatchedBanditTester(new PairwiseAbTest(6), engine, armWeights);
    assertEquals(4, tester.getWinningArm());
  }
}
