package com.wealthfront.thompsonsampling;

import java.util.Objects;

public class ObservedArmPerformance {

  private String variantName;
  private long successes;
  private long failures;

  public ObservedArmPerformance(String variantName, long successes, long failures) {
    this.variantName = variantName;
    this.successes = successes;
    this.failures = failures;
  }

  public long getSuccesses() {
    return successes;
  }

  public long getFailures() {
    return failures;
  }

  public String getVariantName() {
    return variantName;
  }

  public long getTotal() {
    return successes + failures;
  }

  public ObservedArmPerformance add(ObservedArmPerformance that) {
    if (!that.getVariantName().equals(variantName)) {
      throw new IllegalArgumentException(String.format(
          "Cannot add performance of different variant %s! Existing variant: %s",
          that.getVariantName(), variantName));
    }
    successes = successes + that.successes;
    failures = failures + that.failures;
    return this;
  }

  public double getExpectedConversionRate() {
    if (successes + failures == 0) {
      return 0.0;
    }
    return (double) successes / (successes + failures);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObservedArmPerformance that = (ObservedArmPerformance) o;
    return successes == that.successes && failures == that.failures &&
        Objects.equals(variantName, that.variantName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(variantName, successes, failures);
  }

  @Override
  public String toString() {
    return String.format("%s (%d,%d)", variantName, successes, failures);
  }

}
