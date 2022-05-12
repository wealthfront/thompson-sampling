package com.wealthfront.thompsonsampling;

import java.util.List;

public interface WeightGenerator {

  WeightUpdate getWeightUpdate(List<ObservedArmPerformance> performances);

}
