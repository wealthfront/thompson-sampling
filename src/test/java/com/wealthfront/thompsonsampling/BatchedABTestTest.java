package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.*;

public class BatchedABTestTest {
    @Test
    public void testCorrectArmChosen() {
        int correct = 0;
        for (int i = 0; i <= 10000; i++) {
            RandomEngine engine = new MersenneTwister(i);
            BanditPerformance performance = new BanditPerformance(2);
            BatchedABTest batchedBandit = new BatchedABTest();
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
        BanditPerformance performance = new BanditPerformance(Lists.newArrayList(new ObservedArmPerformance(100L, 0L),
                new ObservedArmPerformance(0L, 100L)));
        BatchedABTest batchedABTest = new BatchedABTest();
        batchedABTest.setRequiresMinSamples(false);
        assertEquals(new Integer(0), batchedABTest.getBanditStatistics(performance).getVictoriousArm().get());
        performance = new BanditPerformance(Lists.newArrayList(new ObservedArmPerformance(0L, 100L),
                new ObservedArmPerformance(100L, 0L)));
        batchedABTest = new BatchedABTest();
        batchedABTest.setRequiresMinSamples(false);
        assertEquals(new Integer(1), batchedABTest.getBanditStatistics(performance).getVictoriousArm().get());
        performance = new BanditPerformance(Lists.newArrayList(new ObservedArmPerformance(5L, 5L),
                new ObservedArmPerformance(5L, 5L)));
        batchedABTest = new BatchedABTest();
        batchedABTest.setRequiresMinSamples(false);
        assertFalse(batchedABTest.getBanditStatistics(performance).getVictoriousArm().isPresent());
    }
}
