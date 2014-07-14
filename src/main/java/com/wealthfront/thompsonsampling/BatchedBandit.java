package com.wealthfront.thompsonsampling;

import java.util.List;

public interface BatchedBandit {

  public BanditStatistics getBanditStatistics(BanditPerformance performance);

}
