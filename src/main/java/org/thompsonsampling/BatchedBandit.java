package org.thompsonsampling;

import java.util.List;

public interface BatchedBandit {

  public void update(List<ObservedArmPerformance> performances);

  BanditStatistics getBanditStatistics();
}
