package org.thompsonsampling;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ObservedArmPerformanceTest {

  @Test
  public void testAdd() {
    ObservedArmPerformance a1 = new ObservedArmPerformance(2, 3);
    ObservedArmPerformance a2 = new ObservedArmPerformance(4, 5);
    assertEquals(new ObservedArmPerformance(6, 8), a1.add(a2));
  }

  @Test
  public void testToString()  {
    assertEquals("(2,3)", new ObservedArmPerformance(2, 3).toString());
  }
}
