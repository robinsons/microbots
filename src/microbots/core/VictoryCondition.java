package microbots.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSetMultimap.toImmutableSetMultimap;

import com.google.common.collect.ImmutableList;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A predicate which can be evaluated for a given {@link Simulation} to determine if any {@link
 * Microbot} has prevailed.
 */
@FunctionalInterface
public interface VictoryCondition extends Predicate<Simulation> {

  /**
   * Returns whether or not the specified {@link Simulation} satisfies this condition, and can
   * therefore be stopped.
   */
  boolean isSatisfied(Simulation simulation);

  @Override
  default boolean test(Simulation simulation) {
    return isSatisfied(simulation);
  }

  /**
   * Returns a new {@link VictoryCondition} that requires both {@code this} and {@code other} to be
   * satisfied.
   */
  default VictoryCondition and(VictoryCondition other) {
    checkNotNull(other);
    return simulation -> this.isSatisfied(simulation) && other.isSatisfied(simulation);
  }

  /**
   * Returns a new {@link VictoryCondition} that requires at least one of {@code this} or {@code
   * other} to be satisfied.
   */
  default VictoryCondition or(VictoryCondition other) {
    checkNotNull(other);
    return simulation -> this.isSatisfied(simulation) || other.isSatisfied(simulation);
  }

  /** Returns a new {@link VictoryCondition} that will never be satisfied. */
  static VictoryCondition hamilton() {
    return simulation -> false;
  }

  /**
   * Returns a new {@link VictoryCondition} that requires the specified {@code elapsedTimeInMillis}
   * to pass.
   */
  static VictoryCondition elapsedTime(long elapsedTimeInMillis) {
    checkArgument(elapsedTimeInMillis > 0L, "elapsedTimeInMillis must be positive.");
    return simulation -> simulation.elapsedTimeInMillis() >= elapsedTimeInMillis;
  }

  /**
   * Returns a new {@link VictoryCondition} that is satisfied when one {@link Microbot} population
   * meets or exceeds the designated {@code thresholdPercentage} of the global population. The
   * percentage should be in the range [0,1].
   */
  static VictoryCondition populationThreshold(double thresholdPercentage) {
    checkArgument(
        thresholdPercentage >= 0 && thresholdPercentage <= 1,
        "thresholdPercentage must be in the range [0,1].");
    return simulation -> {
      ImmutableList<Microbot> microbots = simulation.microbots();
      long globalPopulation = microbots.size();
      long thresholdPopulation = (long) (thresholdPercentage * globalPopulation);
      return microbots
          .stream()
          .collect(toImmutableSetMultimap(Microbot::name, Function.identity()))
          .asMap()
          .entrySet()
          .stream()
          .anyMatch(entry -> entry.getValue().size() >= thresholdPopulation);
    };
  }
}
