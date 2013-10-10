package org.thompsonsampling;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArmPerformanceTest {

  @Test
  public void testAdd() {
    ArmPerformance a1 = new ArmPerformance(2, 3);
    ArmPerformance a2 = new ArmPerformance(4, 5);
    assertEquals(new ArmPerformance(6, 8), a1.add(a2));
  }

  @Test
  public void testToString()  {
    assertEquals("(2,3)", new ArmPerformance(2, 3).toString());
  }
}
