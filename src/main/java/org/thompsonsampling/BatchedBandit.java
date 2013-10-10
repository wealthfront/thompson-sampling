package org.thompsonsampling;

import java.util.List;

public interface BatchedBandit {

  public void update(List<ArmPerformance> performances);

  public List<Double> getArmWeights();

}
