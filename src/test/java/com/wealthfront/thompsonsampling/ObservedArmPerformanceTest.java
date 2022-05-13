package com.wealthfront.thompsonsampling;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ObservedArmPerformanceTest {

  @Test(expected = IllegalArgumentException.class)
  public void add_differentVariant_throws() {
    ObservedArmPerformance performance = new ObservedArmPerformance("a", 10, 10);
    performance.add(new ObservedArmPerformance("b", 5, 7));
  }

  @Test
  public void add() {
    ObservedArmPerformance performance = new ObservedArmPerformance("a", 10, 10);
    performance = performance.add(new ObservedArmPerformance("a", 0, 0));
    assertEquals(10, performance.getSuccesses());
    assertEquals(10, performance.getFailures());
    assertEquals(20, performance.getTotal());

    performance = performance.add(new ObservedArmPerformance("a", 5, 2));
    assertEquals(15, performance.getSuccesses());
    assertEquals(12, performance.getFailures());
    assertEquals(27, performance.getTotal());

    performance = performance.add(new ObservedArmPerformance("a", 3, 7));
    assertEquals(18, performance.getSuccesses());
    assertEquals(19, performance.getFailures());
    assertEquals(37, performance.getTotal());
  }

  @Test
  public void getExpectedConversionRate_noSamples_returnsZero() {
    assertEquals(0.0, new ObservedArmPerformance("", 0, 0).getExpectedConversionRate(), 0.001);
  }

  @Test
  public void getExpectedConversionRate() {
    assertEquals(0.0, new ObservedArmPerformance("", 0, 5).getExpectedConversionRate(), 0.001);
    assertEquals(1.0, new ObservedArmPerformance("", 4, 0).getExpectedConversionRate(), 0.001);
    assertEquals(0.5, new ObservedArmPerformance("", 100, 100).getExpectedConversionRate(), 0.001);
  }

}
