package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

import static java.lang.Math.sqrt;
import static java.lang.Math.round;
import static cern.jet.stat.Probability.chiSquare;

public class BatchedABTest extends BaseBatchedBandit {
  private List<Double> weights;
  private RandomEngine randomEngine = new MersenneTwister(new Date());
  private double confidenceLevel = 0.95;
  private double baselineConversionRate = 0.01;
  private double minimumDetectableEffect = 0.05;
  private double statisticalPower = 0.80;
  private boolean requiresMinSamples = true;

  public BatchedABTest(List<ObservedArmPerformance> performances) {
    super(performances);
    setWeights();
  }

  public BatchedABTest(int numberOfArms) {
    super(numberOfArms);
    setWeights();
  }

  private void setWeights() {
    weights = Lists.newArrayList();
    int n = performances.size();
    for (int i = 0; i < n; i++) {
      weights.add(1.0 / n);
    }
  }

  public RandomEngine getRandomEngine() {
    return randomEngine;
  }

  public void setRandomEngine(RandomEngine randomEngine) {
    this.randomEngine = randomEngine;
  }

  public double getConfidenceLevel() {
    return confidenceLevel;
  }

  public void setConfidenceLevel(double confidenceLevel) {
    this.confidenceLevel = confidenceLevel;
  }

  public double getBaselineConversionRate() {
    return baselineConversionRate;
  }

  public void setBaselineConversionRate(double baselineConversionRate) {
    this.baselineConversionRate = baselineConversionRate;
  }

  public List<Double> getWeights() {
    return weights;
  }

  public void setWeights(List<Double> weights) {
    this.weights = weights;
  }

  public double getMinimumDetectableEffect() {
    return minimumDetectableEffect;
  }

  public void setMinimumDetectableEffect(double minimumDetectableEffect) {
    this.minimumDetectableEffect = minimumDetectableEffect;
  }

  public double getStatisticalPower() {
    return statisticalPower;
  }

  public void setStatisticalPower(double statisticalPower) {
    this.statisticalPower = statisticalPower;
  }

  public boolean requiresMinSamples() {
    return requiresMinSamples;
  }

  public void setRequiresMinSamples(boolean requiresMinSamples) {
    this.requiresMinSamples = requiresMinSamples;
  }

  private double square(double x) {
    return x * x;
  }

  private int numberOfSamples() {
    double variance = baselineConversionRate * (1 - baselineConversionRate);
    double deltaSquared = square(baselineConversionRate * minimumDetectableEffect);
    double n = 16 * variance / deltaSquared;
    return (int)round(n);
   }

  @Override
  public BanditStatistics getBanditStatistics() {
    if (requiresMinSamples) {
      int n = numberOfSamples();
      for (ObservedArmPerformance p : performances) {
        if (p.getFailures() + p.getSuccesses() < n) {
          return new BanditStatistics(weights, Optional.<Integer>absent());
        }
      }
    }
    int t = performances.size();
    int bestArm = -1;
    double bestConversion = -1.0;
    List<Long> groupTotals = Lists.newArrayList();
    long totalSuccesses = 0;
    long totalFailures = 0;
    for (int i = 0; i < t; i++) {
      ObservedArmPerformance p = performances.get(i);
      totalSuccesses += p.getSuccesses();
      totalFailures += p.getFailures();
      long total = totalSuccesses + totalFailures;
      groupTotals.add(total);
      double conversion = p.getSuccesses() * 1.0 / (p.getFailures() + p.getSuccesses());
      if (conversion > bestConversion) {
        bestConversion = conversion;
        bestArm = i;
      }
    }
    long totalSamples = totalSuccesses + totalFailures;
    double chiSquared = 0.0;
    for (int i = 0; i < t; i++) {
      ObservedArmPerformance p = performances.get(i);
      double samples = 1.0 * (p.getFailures() + p.getSuccesses());
      double expectedFailure = samples * totalFailures / totalSamples;
      double expectedSuccess = samples * totalSuccesses / totalSamples;
      if (expectedFailure < 5 || expectedSuccess < 5) {
        return new BanditStatistics(weights, Optional.<Integer>absent());
      }
      double failFactor = square(p.getFailures() - expectedFailure) / expectedFailure;
      double successFactor = square(p.getSuccesses() - expectedSuccess) / expectedSuccess;
      chiSquared += failFactor + successFactor;
    }
    if (chiSquare(1.0, chiSquared) >= confidenceLevel) {
      return new BanditStatistics(weights, Optional.of(bestArm));
    }
    return new BanditStatistics(weights, Optional.<Integer>absent());
  }
}
