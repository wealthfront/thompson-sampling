package org.thompsonsampling;

public class ArmPerformance {
  public long getSuccesses() {
    return successes;
  }

  public long getFailures() {
    return failures;
  }

  private final long successes;
  private final long failures;

  public ArmPerformance(long successes, long failures) {
    this.successes = successes;
    this.failures = failures;
  }

  public ArmPerformance add(ArmPerformance that) {
    return new ArmPerformance(successes + that.successes, failures + that.failures);
  }

  @Override
  public int hashCode() {
    return 997 * Long.valueOf(successes).hashCode() ^ 991 * Long.valueOf(failures).hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ArmPerformance) {
      ArmPerformance that = (ArmPerformance)obj;
      return successes == that.successes && failures == that.failures;
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format("(%d,%d)", successes, failures);
  }
}
