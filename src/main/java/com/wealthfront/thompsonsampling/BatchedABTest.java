package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

import static cern.jet.stat.Probability.chiSquare;
import static cern.jet.stat.Probability.normalInverse;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

public class BatchedABTest implements BatchedBandit {
    private RandomEngine randomEngine = new MersenneTwister(new Date());
    private double confidenceLevel = 0.95;
    private double baselineConversionRate = 0.01;
    private double minimumDetectableEffect = 0.5;
    private double statisticalPower = 0.80;
    private boolean requiresMinSamples = true;


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
        double p = baselineConversionRate;
        double delta = baselineConversionRate * minimumDetectableEffect;
        double alpha = 1.0 - confidenceLevel;
        double beta = 1.0 - statisticalPower;
        double v1 = p * (1 - p);
        double v2 = (p + delta) * (1 - p - delta);
        double sd1 = sqrt(2 * v1);
        double sd2 = sqrt(v1 + v2);
        double tAlpha2 = normalInverse(alpha / 2.0);
        double tBeta = normalInverse(beta);
        double n = square(tAlpha2 * sd1 + tBeta * sd2) / square(delta);
        return (int) round(n);
    }

    private List<Double> getWeights(int arms) {
        List<Double> weights = Lists.newArrayListWithCapacity(arms);
        for (int i = 0; i < arms; i++) {
            weights.add(1.0 / arms);
        }
        return weights;
    }

    @Override
    public BanditStatistics getBanditStatistics(BanditPerformance performance) {
        List<ObservedArmPerformance> performances = performance.getPerformances();
        if (requiresMinSamples) {
            int n = numberOfSamples();
            for (ObservedArmPerformance p : performances) {
                if (p.getFailures() + p.getSuccesses() < n) {
                    return new BanditStatistics(getWeights(performances.size()), Optional.<Integer>absent());
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
                return new BanditStatistics(getWeights(performances.size()), Optional.<Integer>absent());
            }
            double failFactor = square(p.getFailures() - expectedFailure) / expectedFailure;
            double successFactor = square(p.getSuccesses() - expectedSuccess) / expectedSuccess;
            chiSquared += failFactor + successFactor;
        }
        if (chiSquare(1.0, chiSquared) >= confidenceLevel) {
            return new BanditStatistics(getWeights(performances.size()), Optional.of(bestArm));
        }
        if (requiresMinSamples) {
            return new BanditStatistics(getWeights(performances.size()), Optional.of(bestArm));
        } else {
            return new BanditStatistics(getWeights(performances.size()), Optional.<Integer>absent());
        }
    }
}
