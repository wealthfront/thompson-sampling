package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.junit.Assert.*;

public class BatchedThompsonSamplingTest {
    @Test
    public void testUpdate() {
        BanditPerformance performance = new BanditPerformance(2);
        BatchedThompsonSampling bandit = new BatchedThompsonSampling();
        performance.update(Lists.newArrayList(new ObservedArmPerformance(1, 2), new ObservedArmPerformance(3, 4)));
        assertEquals(Lists.newArrayList(new ObservedArmPerformance(1, 2), new ObservedArmPerformance(3, 4)), performance.getPerformances());
        performance.update(Lists.newArrayList(new ObservedArmPerformance(1, 2), new ObservedArmPerformance(3, 4)));
        assertEquals(Lists.newArrayList(new ObservedArmPerformance(2, 4), new ObservedArmPerformance(6, 8)), performance.getPerformances());
        try {
            performance.update(Lists.newArrayList(new ObservedArmPerformance(1, 2), new ObservedArmPerformance(3, 4), new ObservedArmPerformance(5, 6)));
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    @Ignore
    public void testCorrectArmChosen() {
        int correct = 0;
        for (int i = 0; i < 10000; i++) {
            RandomEngine engine = new MersenneTwister(i);
            BatchedThompsonSampling batchedBandit = new BatchedThompsonSampling();
            batchedBandit.setRandomEngine(engine);
            BatchedBanditTester tester = new BatchedBanditTester(batchedBandit, engine);
            if (i % 100 == 0) {
                System.out.println("Batches complete " + i);
            }
            correct += tester.getWinningArm();
        }
        System.out.println(correct);
        assertTrue(correct > 9500);
    }

    @Test
    public void testPerformance() {
        int maxBanditIterations = 0;
        double maxBanditRegret = 0.0;
        for (int i = 51; i <= 60; i++) {
            RandomEngine engine = new MersenneTwister(i);
            BanditPerformance performance = new BanditPerformance(2);
            BatchedThompsonSampling batchedBandit = new BatchedThompsonSampling();
            batchedBandit.setRandomEngine(engine);
            BatchedBanditTester tester = new BatchedBanditTester(batchedBandit, engine);
            double regret = performance.cumulativeRegret(0.015, Lists.newArrayList(0.01, 0.015));
            maxBanditIterations = max(maxBanditIterations, tester.getIterations());
            maxBanditRegret = max(maxBanditRegret, regret);
            assertEquals(1, tester.getWinningArm());
        }
        int minAbIterations = Integer.MAX_VALUE;
        double minAbRegret = Double.MAX_VALUE;
        for (int i = 51; i <= 60; i++) {
            RandomEngine engine = new MersenneTwister(i);
            BanditPerformance performance = new BanditPerformance(2);
            BatchedABTest batchedBandit = new BatchedABTest();
            batchedBandit.setRandomEngine(engine);
            BatchedBanditTester tester = new BatchedBanditTester(batchedBandit, engine);
            double regret = performance.cumulativeRegret(0.015, Lists.newArrayList(0.01, 0.015));
            minAbIterations = min(minAbIterations, tester.getIterations());
            minAbRegret = min(minAbRegret, regret);
            assertEquals(1, tester.getWinningArm());
        }
    }

    @Test
    public void testPerformance2() {
        int maxBanditIterations = 0;
        double maxBanditRegret = 0.0;
        for (int i = 51; i <= 60; i++) {
            RandomEngine engine = new MersenneTwister(i);
            BanditPerformance performance = new BanditPerformance(6);
            BatchedThompsonSampling batchedBandit = new BatchedThompsonSampling();
            batchedBandit.setRandomEngine(engine);
            BatchedBanditTester tester = new BatchedBanditTester(batchedBandit, engine,
                    Lists.newArrayList(new BernouliArm(0.04, engine),
                            new BernouliArm(0.05, engine),
                            new BernouliArm(0.045, engine),
                            new BernouliArm(0.03, engine),
                            new BernouliArm(0.02, engine),
                            new BernouliArm(0.035, engine)));
            double regret = performance.cumulativeRegret(0.05, Lists.newArrayList(0.04, 0.05, 0.045, 0.03, 0.02, 0.035));
            maxBanditIterations = max(maxBanditIterations, tester.getIterations());
            maxBanditRegret = max(maxBanditRegret, regret);
        }
        int minAbIterations = Integer.MAX_VALUE;
        double minAbRegret = Double.MAX_VALUE;
        for (int i = 51; i <= 60; i++) {
            RandomEngine engine = new MersenneTwister(i);
            BanditPerformance performance = new BanditPerformance(6);
            BatchedABTest batchedBandit = new BatchedABTest();
            batchedBandit.setRandomEngine(engine);
            BatchedBanditTester tester = new BatchedBanditTester(batchedBandit, engine,
                    Lists.newArrayList(new BernouliArm(0.04, engine),
                            new BernouliArm(0.05, engine),
                            new BernouliArm(0.045, engine),
                            new BernouliArm(0.03, engine),
                            new BernouliArm(0.02, engine),
                            new BernouliArm(0.035, engine)));
            double regret = performance.cumulativeRegret(0.05, Lists.newArrayList(0.04, 0.05, 0.045, 0.03, 0.02, 0.35));
            minAbIterations = min(minAbIterations, tester.getIterations());
            minAbRegret = min(minAbRegret, regret);
        }
        System.out.println("Min A/B regret: " + minAbRegret);
        System.out.println("Max Bandit regret: " + maxBanditRegret);
        System.out.println("Min A/B # batches (batch size = 100 samples): " + minAbIterations);
        System.out.println("Max Bandit # batches (batch size = 100 samples): " + maxBanditIterations);
    }
}
