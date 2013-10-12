package org.thompsonsampling;

import java.util.List;

public interface BatchedBandit {

  public List<ObservedArmPerformance> getPerformances();

  public void update(List<ObservedArmPerformance> newPerformances);

  public BanditStatistics getBanditStatistics();
}
