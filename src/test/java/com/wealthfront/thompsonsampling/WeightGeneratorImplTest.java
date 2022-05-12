package com.wealthfront.thompsonsampling;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class WeightGeneratorImplTest {

  private final BatchedBandit bandit = mock(BatchedBandit.class);

  @Test
  public void getWeightUpdate_emptyList() {
    when(bandit.getBanditStatistics(new BanditPerformance(emptyList())))
        .thenReturn(new BanditStatistics(emptyMap(), Optional.empty()));

    WeightUpdate weightUpdate = getGenerator().getWeightUpdate(emptyList());
    assertEquals(0, weightUpdate.getWeightByVariantName().size());
    assertFalse(weightUpdate.getMaybeWinningVariant().isPresent());
    assertEquals(0, weightUpdate.getPerformancesByVariantName().keySet().size());
  }

  @Test
  public void getWeightUpdate() {
    ObservedArmPerformance armPerformanceA = new ObservedArmPerformance("a", 10, 10);
    ObservedArmPerformance armPerformanceB = new ObservedArmPerformance("b", 9, 11);
    ObservedArmPerformance armPerformanceC = new ObservedArmPerformance("c", 8, 12);
    List<ObservedArmPerformance> armPerformances = ImmutableList.of(armPerformanceA, armPerformanceB, armPerformanceC);

    when(bandit.getBanditStatistics(new BanditPerformance(armPerformances)))
        .thenReturn(new BanditStatistics(ImmutableMap.of("a", 0.45, "b", 0.35, "c", 0.20), Optional.empty()));

    WeightUpdate weightUpdate = getGenerator().getWeightUpdate(armPerformances);
    assertEquals(ImmutableMap.of("a", 0.45, "b", 0.35, "c", 0.20), weightUpdate.getWeightByVariantName());
    assertFalse(weightUpdate.getMaybeWinningVariant().isPresent());
    assertEquals(ImmutableMap.of("a", armPerformanceA, "b", armPerformanceB, "c", armPerformanceC),
        weightUpdate.getPerformancesByVariantName());
  }

  @Test
  public void getWeightUpdate_withWinningVariant() {
    ObservedArmPerformance armPerformanceA = new ObservedArmPerformance("a", 10, 5);
    ObservedArmPerformance armPerformanceB = new ObservedArmPerformance("b", 6, 9);
    ObservedArmPerformance armPerformanceC = new ObservedArmPerformance("c", 4, 11);
    List<ObservedArmPerformance> armPerformances = ImmutableList.of(armPerformanceA, armPerformanceB, armPerformanceC);

    when(bandit.getBanditStatistics(new BanditPerformance(armPerformances)))
        .thenReturn(new BanditStatistics(ImmutableMap.of("a", 0.9, "b", 0.09, "c", 0.01), Optional.of("a")));

    WeightUpdate weightUpdate = getGenerator().getWeightUpdate(armPerformances);
    assertEquals(ImmutableMap.of("a", 0.9, "b", 0.09, "c", 0.01), weightUpdate.getWeightByVariantName());
    assertEquals("a", weightUpdate.getMaybeWinningVariant().get());
    assertEquals(ImmutableMap.of("a", armPerformanceA, "b", armPerformanceB, "c", armPerformanceC),
        weightUpdate.getPerformancesByVariantName());
  }

  private WeightGeneratorImpl getGenerator() {
    WeightGeneratorImpl generator = new WeightGeneratorImpl();
    generator.bandit = bandit;
    return generator;
  }
  
}
