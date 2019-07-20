package microbots.core.model;

/** Represents the predefined speeds at which a {@link Simulation} may be run. */
public enum SimulationRate {
  NORMAL(100L, "Normal - 100 ms"),
  FAST(50L, "Fast - 50 ms"),
  FASTER(16L, "Faster - 16 ms"),
  FASTEST(5L, "Fastest - 5 ms");

  private final long millisPerRound;
  private final String description;

  SimulationRate(long millisPerRound, String description) {
    this.millisPerRound = millisPerRound;
    this.description = description;
  }

  /** Returns the number of milliseconds that this rate allots per simulation round. */
  long millisPerRound() {
    return millisPerRound;
  }

  /** Returns a textual representation of this rate, suitable for displaying in the UI. */
  public String description() {
    return description;
  }
}
