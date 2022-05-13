package com.wealthfront.thompsonsampling;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cern.jet.stat.Probability.normalInverse;
import static java.lang.Math.round;
import static cern.jet.stat.Probability.chiSquare;
import static java.lang.Math.sqrt;
import static java.util.stream.Collectors.toMap;

import com.google.common.annotations.VisibleForTesting;

public class BatchedABTest implements BatchedBandit {

  private static boolean REQUIRES_MIN_SAMPLES = true;
  private static final double CONFIDENCE_LEVEL = 0.95;
  private static final double BASELINE_CONVERSION_RATE = 0.01;
  private static final double MINIMUM_DETECTABLE_EFFECT = 0.5;
  private static final double STATISTICAL_POWER = 0.80;
  private static final int MIN_EXPECTED_SUCCESS = 5;
  private static final int MIN_EXPECTED_FAILURE = 5;

  @Override
  public BanditStatistics getBanditStatistics(BanditPerformance banditPerformance) {
    List<ObservedArmPerformance> performances = banditPerformance.getPerformances();
    Map<String, Double> equalWeightsByVariant = getEqualWeightsByVariant(performances);
    if (REQUIRES_MIN_SAMPLES) {
      boolean isBelowMinimumSamples = performances.stream()
          .anyMatch(performance -> performance.getTotal() < getMinimumNumberOfSamples());
      if (isBelowMinimumSamples) {
        return new BanditStatistics(getEqualWeightsByVariant(performances), Optional.empty());
      }
    }

    MeasuredPerformance measuredPerformance = getMeasuredPerformance(performances);
    long totalSamples = measuredPerformance.totalSuccesses + measuredPerformance.totalFailures;
    double chiSquared = 0.0;
    for (ObservedArmPerformance performance : performances) {
      double samples = 1.0 * performance.getTotal();
      double expectedFailure = samples * measuredPerformance.totalFailures / totalSamples;
      double expectedSuccess = samples * measuredPerformance.totalSuccesses / totalSamples;
      if (expectedFailure < MIN_EXPECTED_FAILURE || expectedSuccess < MIN_EXPECTED_SUCCESS) {
        return new BanditStatistics(equalWeightsByVariant, Optional.empty());
      }
      double failFactor = square(performance.getFailures() - expectedFailure) / expectedFailure;
      double successFactor = square(performance.getSuccesses() - expectedSuccess) / expectedSuccess;
      chiSquared += failFactor + successFactor;
    }

    boolean includeVictoriousVariant = chiSquare(1.0, chiSquared) >= CONFIDENCE_LEVEL || REQUIRES_MIN_SAMPLES;
    Optional<String> maybeVictoriousVariant = includeVictoriousVariant
        ? Optional.of(measuredPerformance.bestVariant)
        : Optional.empty();
    return new BanditStatistics(equalWeightsByVariant, maybeVictoriousVariant);
  }

  private int getMinimumNumberOfSamples() {
    double p = BASELINE_CONVERSION_RATE;
    double delta = BASELINE_CONVERSION_RATE * MINIMUM_DETECTABLE_EFFECT;
    double alpha = 1.0 - CONFIDENCE_LEVEL;
    double beta = 1.0 - STATISTICAL_POWER;
    double v1 = p * (1 - p);
    double v2 = (p + delta) * (1 - p - delta);
    double sd1 = sqrt(2 * v1);
    double sd2 = sqrt(v1 + v2);
    double tAlpha2 = normalInverse(alpha / 2.0);
    double tBeta = normalInverse(beta);
    double n = square(tAlpha2 * sd1 + tBeta * sd2) / square(delta);
    return (int) round(n);
  }

  private double square(double x) {
    return x * x;
  }

  private Map<String, Double> getEqualWeightsByVariant(List<ObservedArmPerformance> performances) {
    double equalWeight = 1.0 / performances.size();
    return performances.stream()
        .collect(toMap(ObservedArmPerformance::getVariantName, x -> equalWeight));
  }

  private MeasuredPerformance getMeasuredPerformance(List<ObservedArmPerformance> performances) {
    MeasuredPerformance measuredPerformance = new MeasuredPerformance();
    performances.forEach(performance -> {
      measuredPerformance.totalSuccesses += performance.getSuccesses();
      measuredPerformance.totalFailures += performance.getFailures();
      double conversion = performance.getSuccesses() * 1.0 / (performance.getFailures() + performance.getSuccesses());
      if (conversion > measuredPerformance.bestConversion) {
        measuredPerformance.bestConversion = conversion;
        measuredPerformance.bestVariant = performance.getVariantName();
      }
    });
    return measuredPerformance;
  }

  @VisibleForTesting
  void setRequiresMinSamples(boolean requiresMinSamples) {
    REQUIRES_MIN_SAMPLES = requiresMinSamples;
  }

  private static class MeasuredPerformance {

    String bestVariant;
    double bestConversion = -1.0;
    long totalSuccesses = 0;
    long totalFailures = 0;

  }

}
