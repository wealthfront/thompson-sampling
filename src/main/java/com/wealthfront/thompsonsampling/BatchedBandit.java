package com.wealthfront.thompsonsampling;

public interface BatchedBandit {

  BanditStatistics getBanditStatistics(BanditPerformance performance);

}
