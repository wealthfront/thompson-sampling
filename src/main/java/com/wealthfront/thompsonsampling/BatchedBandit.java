package com.wealthfront.thompsonsampling;

public interface BatchedBandit {

    public BanditStatistics getBanditStatistics(BanditPerformance performance);

}
