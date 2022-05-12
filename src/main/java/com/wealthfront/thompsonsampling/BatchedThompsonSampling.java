package com.wealthfront.thompsonsampling;

import static java.util.stream.Collectors.toList;

import cern.jet.random.Beta;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BatchedThompsonSampling implements BatchedBandit {

  private static RandomEngine RANDOM_ENGINE = new MersenneTwister(new Date());

  private static final int NUMBER_OF_DRAWS = 10_000;
  private static final double CONFIDENCE_LEVEL = 0.95;
  private static final double EXPERIMENT_VALUE_QUIT_LEVEL = 0.01;

  @Override
  public BanditStatistics getBanditStatistics(BanditPerformance performance) {
    List<ObservedArmPerformance> performances = performance.getPerformances();
    int n = performances.size();
    double[][] table = new double[getNumberOfDraws()][n];
    int[] wins = new int[n];
    List<Beta> probabilityDensityFunctions = getProbabilityDensityFunctions(performances);

    for (int i = 0; i < getNumberOfDraws(); i++) {
      double maxValue = -1.0;
      int winningArm = -1;
      for (int j = 0; j < n; j++) {
        Beta pdf = probabilityDensityFunctions.get(j);
        table[i][j] = pdf.nextDouble();
        if (table[i][j] > maxValue) {
          maxValue = table[i][j];
          winningArm = j;
        }
      }
      wins[winningArm] += 1;
    }

    Map<String, Double> armWeightsByVariant = new HashMap<>();
    int bestArm = -1;
    double bestWeight = -1.0;
    for (int i = 0; i < n; i++) {
      double weight = (1.0 * wins[i]) / getNumberOfDraws();
      if (weight > bestWeight) {
        bestWeight = weight;
        bestArm = i;
      }
      armWeightsByVariant.put(performances.get(i).getVariantName(), weight);
    }
    if (bestWeight > getConfidenceLevel()) {
      return new BanditStatistics(
          armWeightsByVariant, Optional.of(performances.get(bestArm).getVariantName()));
    }

    double[] valueRemaining = new double[getNumberOfDraws()];
    for (int i = 0; i < getNumberOfDraws(); i++) {
      double maxValue = -1.0;
      int winningArm = -1;
      for (int j = 0; j < n; j++) {
        if (table[i][j] > maxValue) {
          maxValue = table[i][j];
          winningArm = j;
        }
      }
      if (winningArm == bestArm) {
        valueRemaining[i] = 0.0;
      } else {
        valueRemaining[i] = (maxValue - table[i][bestArm]) / table[i][bestArm];
      }
    }

    Percentile percentile = new Percentile();
    percentile.setData(valueRemaining);
    double likelyValueRemaining = percentile.evaluate(getConfidenceLevel() * 100.0);
    Optional<String> victoriousVariant = likelyValueRemaining < getExperimentValueQuitLevel() ?
        Optional.of(performances.get(bestArm).getVariantName()) : Optional.empty();
    return new BanditStatistics(armWeightsByVariant, victoriousVariant);
  }

  @VisibleForTesting
  List<Beta> getProbabilityDensityFunctions(List<ObservedArmPerformance> performances) {
    return performances.stream().map(armPerformance -> {
      double alpha = armPerformance.getSuccesses() + 1;
      double beta = armPerformance.getFailures() + 1;
      return new Beta(alpha, beta, getRandomEngine());
    }).collect(toList());
  }

  @VisibleForTesting
  RandomEngine getRandomEngine() {
    return RANDOM_ENGINE;
  }

  @VisibleForTesting
  int getNumberOfDraws() {
    return NUMBER_OF_DRAWS;
  }

  @VisibleForTesting
  double getConfidenceLevel() {
    return CONFIDENCE_LEVEL;
  }

  @VisibleForTesting
  double getExperimentValueQuitLevel() {
    return EXPERIMENT_VALUE_QUIT_LEVEL;
  }

}
