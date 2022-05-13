package com.wealthfront.thompsonsampling;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.annotations.VisibleForTesting;

public class WeightGeneratorImpl implements WeightGenerator {

  @VisibleForTesting
  BatchedBandit bandit = new BatchedThompsonSampling();

  @Override
  public WeightUpdate getWeightUpdate(List<ObservedArmPerformance> performances) {
    BanditPerformance performance = new BanditPerformance(performances);
    BanditStatistics statistics = bandit.getBanditStatistics(performance);
    Map<String, Double> weightsByVariant = statistics.getWeightsByVariant();
    Map<String, ObservedArmPerformance> performancesByVariant = performances.stream()
        .collect(toMap(ObservedArmPerformance::getVariantName, Function.identity()));
    return new WeightUpdate(weightsByVariant, statistics.getVictoriousVariant(), performancesByVariant);
  }

}
