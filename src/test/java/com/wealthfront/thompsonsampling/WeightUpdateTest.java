package com.wealthfront.thompsonsampling;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class WeightUpdateTest {

  @Test
  public void getters() {
    Map<String, Double> weightByVariantName = ImmutableMap.of("a", 0.4, "b", 0.6);
    Map<String, ObservedArmPerformance> performanceByVariantName = ImmutableMap.of(
        "a", new ObservedArmPerformance("a", 10L, 5L),
        "b", new ObservedArmPerformance("b", 8L, 5L));

    WeightUpdate weightUpdate = new WeightUpdate(weightByVariantName, Optional.of("a"), performanceByVariantName);
    assertEquals(weightByVariantName, weightUpdate.getWeightByVariantName());
    assertEquals("a", weightUpdate.getMaybeWinningVariant().get());
    assertEquals(performanceByVariantName, weightUpdate.getPerformancesByVariantName());
  }

}
