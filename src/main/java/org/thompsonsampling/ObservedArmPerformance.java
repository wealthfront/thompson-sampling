package org.thompsonsampling;

public class ObservedArmPerformance {
  public long getSuccesses() {
    return successes;
  }

  public long getFailures() {
    return failures;
  }

  private long successes;
  private long failures;

  public ObservedArmPerformance(long successes, long failures) {
    this.successes = successes;
    this.failures = failures;
  }

  public ObservedArmPerformance add(ObservedArmPerformance that) {
    successes = successes + that.successes;
    failures = failures + that.failures;
    return this;
  }

  public ObservedArmPerformance addSuccess() {
    successes = successes + 1;
    return this;
  }

  public ObservedArmPerformance addFailure() {
    failures = failures + 1;
    return this;
  }

  @Override
  public int hashCode() {
    return 997 * Long.valueOf(successes).hashCode() ^ 991 * Long.valueOf(failures).hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ObservedArmPerformance) {
      ObservedArmPerformance that = (ObservedArmPerformance)obj;
      return successes == that.successes && failures == that.failures;
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format("(%d,%d)", successes, failures);
  }
}
