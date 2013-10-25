package com.wealthfront.thompsonsampling;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.List;

public class PairwiseAbTest implements BatchedBandit {

  private BatchedABTest current = new BatchedABTest();
  private List<Integer> currentArms = Lists.newArrayList(0, 1);
  private int next = 2;
  private int numberOfArms;

  @Override
  public BanditStatistics getBanditStatistics(BanditPerformance performance) {
    numberOfArms = performance.getPerformances().size();
    BanditStatistics results = current.getBanditStatistics(performance);
    if (results.getVictoriousArm().isPresent()) {
      int winner = results.getVictoriousArm().get();
      if (currentArms.contains(numberOfArms - 1)) {
        return new BanditStatistics(getWeights(), Optional.of(currentArms.get(winner)));
      } else {
        if (currentArms.get(0) == currentArms.get(winner)) {
          currentArms.set(1, next);
        } else {
          currentArms.set(0, next);
        }
        current = new BatchedABTest();
        next++;
      }
    }
    return new BanditStatistics(getWeights(), Optional.<Integer>absent());
  }

  private List<Double> getWeights() {
    List<Double> resultWeights = Lists.newArrayList();
    for (int i = 0; i < numberOfArms; i++) {
      if (currentArms.contains(i)) {
        resultWeights.add(0.5);
      } else {
        resultWeights.add(0.0);
      }
    }
    return resultWeights;
  }

}
