package org.thompsonsampling;

import cern.jet.random.Beta;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.Date;
import java.util.List;

public class BatchedThompsonSampling implements BatchedBandit {
  final List<ObservedArmPerformance> performances;
  private int numberOfDraws = 10000;
  private RandomEngine randomEngine = new MersenneTwister(new Date());
  private double confidenceLevel = 0.95;
  private double experimentValueQuitLevel = 0.01;

  public BatchedThompsonSampling(List<ObservedArmPerformance> performances) {
    this.performances = performances;
  }

  public BatchedThompsonSampling(int numberOfArms) {
    this(Lists.<ObservedArmPerformance>newArrayListWithCapacity(numberOfArms));
    for (int i = 0; i < numberOfArms; i++) {
      performances.add(new ObservedArmPerformance(0, 0));
    }
  }

  public RandomEngine getRandomEngine() {
    return randomEngine;
  }

  public void setRandomEngine(RandomEngine randomEngine) {
    this.randomEngine = randomEngine;
  }

  public int getNumberOfDraws() {
    return numberOfDraws;
  }

  public void setNumberOfDraws(int numberOfDraws) {
    this.numberOfDraws = numberOfDraws;
  }

  public double getConfidenceLevel() {
    return confidenceLevel;
  }

  public void setConfidenceLevel(double confidenceLevel) {
    this.confidenceLevel = confidenceLevel;
  }

  public double getExperimentValueQuitLevel() {
    return experimentValueQuitLevel;
  }

  public void setExperimentValueQuitLevel(double experimentValueQuitLevel) {
    this.experimentValueQuitLevel = experimentValueQuitLevel;
  }

  @Override
  public List<ObservedArmPerformance> getPerformances() {
    return performances;
  }

  @Override
  public void update(List<ObservedArmPerformance> newPerformances) {
    if (newPerformances == null || newPerformances.size() != performances.size()) {
      throw new IllegalArgumentException(String.format("Wrong number of arms given: expected %d.",
          performances.size()));
    }
    for (int i = 0; i < newPerformances.size(); i++) {
      performances.set(i, performances.get(i).add(newPerformances.get(i)));
    }
  }

  @Override
  public BanditStatistics getBanditStatistics() {
    int n = performances.size();
    List<Beta> pdfs = FluentIterable.from(performances).transform(new Function<ObservedArmPerformance, Beta>() {
      @Override
      public Beta apply(ObservedArmPerformance armPerformance) {
        double alpha = armPerformance.getSuccesses() + 1;
        double beta = armPerformance.getFailures() + 1;
        return new Beta(alpha, beta, randomEngine);
      }
    }).toList();
    double[][] table = new double[numberOfDraws][n];
    int[] wins = new int[n];
    for (int i = 0; i < numberOfDraws; i++) {
      double maxValue = -1.0;
      int winningArm = -1;
      for (int j = 0; j < n; j++) {
        Beta pdf = pdfs.get(j);
        table[i][j] = pdf.nextDouble();
        if (table[i][j] > maxValue) {
          maxValue = table[i][j];
          winningArm = j;
        }
      }
      wins[winningArm] += 1;
    }
    List<Double> armWeights = Lists.newArrayList();
    int bestArm = -1;
    double bestWeight = -1.0;
    for (int j = 0; j < n; j++) {
      double weight = (1.0 * wins[j]) / numberOfDraws;
      if (weight > bestWeight) {
        bestWeight = weight;
        bestArm = j;
      }
      armWeights.add(weight);
    }
    /*if (bestWeight > confidenceLevel) {
      return new BanditStatistics(armWeights, Optional.of(bestArm));
    }*/
    double[] valueRemaining = new double[numberOfDraws];
    for (int i = 0; i < numberOfDraws; i++) {
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
    double likelyValueRemaining = percentile.evaluate(confidenceLevel * 100.0);
    if (likelyValueRemaining < experimentValueQuitLevel) {
      return new BanditStatistics(armWeights, Optional.of(bestArm));
    }
    return new BanditStatistics(armWeights, Optional.<Integer>absent());
  }
}
