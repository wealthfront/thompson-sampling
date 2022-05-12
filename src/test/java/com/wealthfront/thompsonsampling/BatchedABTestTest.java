package com.wealthfront.thompsonsampling;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BatchedABTestTest {

  @Test
  public void getBanditStatistics_requiresMinSamples_belowMinSamples_returnsEmptyVictoriousVariant() {
    BanditPerformance performance = new BanditPerformance(Lists.newArrayList(
        new ObservedArmPerformance("a", 5L, 5L),
        new ObservedArmPerformance("b", 5L, 5L)));
    BatchedABTest batchedABTest = new BatchedABTest();
    batchedABTest.setRequiresMinSamples(true);
    BanditStatistics result = batchedABTest.getBanditStatistics(performance);
    assertFalse(batchedABTest.getBanditStatistics(performance).getVictoriousVariant().isPresent());
    assertEquals(ImmutableMap.of("a", 0.5, "b", 0.5), result.getWeightsByVariant());
  }

  @Test
  public void getBanditStatistics_chiSquaredAboveConfidenceLevel_returnsVictoriousVariant() {
    BanditPerformance performance = new BanditPerformance(Lists.newArrayList(
        new ObservedArmPerformance("a", 100L, 0L),
        new ObservedArmPerformance("b", 0L, 100L)));
    BatchedABTest batchedABTest = new BatchedABTest();
    batchedABTest.setRequiresMinSamples(false);
    BanditStatistics result = batchedABTest.getBanditStatistics(performance);
    assertEquals("a", result.getVictoriousVariant().get());
    assertEquals(ImmutableMap.of("a", 0.5, "b", 0.5), result.getWeightsByVariant());

    performance = new BanditPerformance(Lists.newArrayList(
        new ObservedArmPerformance("a", 0L, 100L),
        new ObservedArmPerformance("b", 100L, 0L)));
    batchedABTest = new BatchedABTest();
    batchedABTest.setRequiresMinSamples(false);
    assertEquals("b", batchedABTest.getBanditStatistics(performance).getVictoriousVariant().get());
    assertEquals(ImmutableMap.of("a", 0.5, "b", 0.5), result.getWeightsByVariant());
  }

  @Test
  public void getBanditStatistics_chiSquaredBelowConfidenceLevel_returnsEmptyVictoriousVariant() {
    BanditPerformance performance = new BanditPerformance(Lists.newArrayList(
        new ObservedArmPerformance("a", 5L, 5L),
        new ObservedArmPerformance("b", 5L, 5L)));
    BatchedABTest batchedABTest = new BatchedABTest();
    batchedABTest.setRequiresMinSamples(false);
    BanditStatistics result = batchedABTest.getBanditStatistics(performance);
    assertFalse(batchedABTest.getBanditStatistics(performance).getVictoriousVariant().isPresent());
    assertEquals(ImmutableMap.of("a", 0.5, "b", 0.5), result.getWeightsByVariant());
  }

  @Test
  public void getBanditStatistics_belowMinExpectedFailures_returnsEmptyVictoriousVariant() {
    BanditPerformance performance = new BanditPerformance(Lists.newArrayList(
        new ObservedArmPerformance("a", 5L, 4L),
        new ObservedArmPerformance("b", 5L, 5L)));
    BatchedABTest batchedABTest = new BatchedABTest();
    batchedABTest.setRequiresMinSamples(false);
    BanditStatistics result = batchedABTest.getBanditStatistics(performance);
    assertFalse(batchedABTest.getBanditStatistics(performance).getVictoriousVariant().isPresent());
    assertEquals(ImmutableMap.of("a", 0.5, "b", 0.5), result.getWeightsByVariant());
  }

  @Test
  public void getBanditStatistics_belowMinExpectedSuccesses_returnsEmptyVictoriousVariant() {
    BanditPerformance performance = new BanditPerformance(Lists.newArrayList(
        new ObservedArmPerformance("a", 5L, 5L),
        new ObservedArmPerformance("b", 4L, 5L)));
    BatchedABTest batchedABTest = new BatchedABTest();
    batchedABTest.setRequiresMinSamples(false);
    BanditStatistics result = batchedABTest.getBanditStatistics(performance);
    assertFalse(batchedABTest.getBanditStatistics(performance).getVictoriousVariant().isPresent());
    assertEquals(ImmutableMap.of("a", 0.5, "b", 0.5), result.getWeightsByVariant());
  }

}
