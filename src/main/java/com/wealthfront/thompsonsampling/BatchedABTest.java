package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static java.lang.Math.round;
import static cern.jet.stat.Probability.studentTInverse;

public class BatchedABTest extends BaseBatchedBandit {
  private List<Double> weights;
  private RandomEngine randomEngine = new MersenneTwister(new Date());
  private double confidenceLevel = 0.95;
  private double baselineConversionRate = 0.01;
  private double minimumDetectableEffect = 0.05;
  private double statisticalPower = 0.80;

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

  private double square(double x) {
    return x * x;
  }

  private int numberOfSamples() {
    double variance = baselineConversionRate * (1 - baselineConversionRate);
    double delta_squared = square(baselineConversionRate * minimumDetectableEffect);
    double half_alpha = (1.0 - confidenceLevel) / 2.0;
    double beta = (1.0 - statisticalPower);
    double n = 16 * variance / delta_squared;
    double change = 2.0;
    while (change >= 0.05) {
      double next = 2.0 * square(studentTInverse(half_alpha, (int)round(n)-1) + studentTInverse(beta, (int)round(n)-1))
          * variance / delta_squared;
      change = abs(next - n);
      n = next;
    }
    return (int)round(n);
   }

  @Override
  public BanditStatistics getBanditStatistics() {
    int n = numberOfSamples();
    for (ObservedArmPerformance p : performances) {
      if (p.getFailures() + p.getSuccesses() < n) {
        return new BanditStatistics(weights, Optional.<Integer>absent());
      }
    }
    int t = performances.size();
    List<Double> conversions = Lists.newArrayList();
    List<Double> variances = Lists.newArrayList();
    int bestArm = -1;
    double bestConversion = -1.0;
    double bestVariance = 10000000000.0;
    for (int i = 0; i < t; i++) {
      ObservedArmPerformance perf = performances.get(i);
      long samples = perf.getFailures() + perf.getSuccesses();
      double conversion = 1.0 * perf.getSuccesses() / samples;
      conversions.add(conversion);
      double variance = conversion * (1.0 - conversion) / samples;
      variances.add(variance);
      if (conversion > bestConversion) {
        bestConversion = conversion;
        bestArm = i;
        bestVariance = variance;
      }
    }
    for (int i = 0; i < t; i++) {
      if (i == bestArm) {
        continue;
      }
      double zScore = (bestConversion - conversions.get(i)) /
          sqrt(bestVariance + variances.get(i));
      if (zScore < 0.95) {
        return new BanditStatistics(weights, Optional.<Integer>absent());
      }
    }
    return new BanditStatistics(weights, Optional.of(bestArm));
  }
}
