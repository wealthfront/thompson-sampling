package com.wealthfront.thompsonsampling;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class BanditStatisticsTest {

  @Test
  public void getters() {
    BanditStatistics banditStatistics = new BanditStatistics(
        ImmutableMap.of("a", 0.8, "b", 0.2), Optional.of("a"));
    assertEquals(ImmutableMap.of("a", 0.8, "b", 0.2), banditStatistics.getWeightsByVariant());
    assertEquals("a", banditStatistics.getVictoriousVariant().get());
  }

}
