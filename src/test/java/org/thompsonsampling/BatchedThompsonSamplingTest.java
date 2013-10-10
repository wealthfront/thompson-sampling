package org.thompsonsampling;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.*;

public class BatchedThompsonSamplingTest {
  @Test
  public void testUpdate() {
    BatchedThompsonSampling bandit = new BatchedThompsonSampling(2);
    bandit.update(Lists.newArrayList(new ArmPerformance(1, 2), new ArmPerformance(3, 4)));
    assertEquals(Lists.newArrayList(new ArmPerformance(1, 2), new ArmPerformance(3, 4)), bandit.performances);
    bandit.update(Lists.newArrayList(new ArmPerformance(1, 2), new ArmPerformance(3, 4)));
    assertEquals(Lists.newArrayList(new ArmPerformance(2, 4), new ArmPerformance(6, 8)), bandit.performances);
    try {
      bandit.update(Lists.newArrayList(new ArmPerformance(1, 2), new ArmPerformance(3, 4), new ArmPerformance(5, 6)));
      fail("Expecting IllegalArgumentException");
    } catch (IllegalArgumentException e) { }
  }



  @Test
  public void testGetArmWeights() {

  }
}
