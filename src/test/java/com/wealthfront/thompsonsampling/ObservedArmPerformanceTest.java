package com.wealthfront.thompsonsampling;

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
    public void testAddSuccess() {
        ObservedArmPerformance a1 = new ObservedArmPerformance(2, 3);
        a1.addSuccess();
        assertEquals(new ObservedArmPerformance(3, 3), a1);
    }

    @Test
    public void testAddFailure() {
        ObservedArmPerformance a1 = new ObservedArmPerformance(2, 3);
        a1.addFailure();
        assertEquals(new ObservedArmPerformance(2, 4), a1);
    }

    @Test
    public void testToString() {
        assertEquals("(2,3)", new ObservedArmPerformance(2, 3).toString());
    }
}
