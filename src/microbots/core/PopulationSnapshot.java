package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/** Represents a snapshot of all microbot populations at a given point in time. */
final class PopulationSnapshot {

  private final Arena arena;
  private final ImmutableList<Population> populations;
  private final long creationTimeMillis;

  private PopulationSnapshot(
      Arena arena, ImmutableList<Population> populations, long creationTimeMillis) {
    this.arena = arena;
    this.populations = populations;
    this.creationTimeMillis = creationTimeMillis;
  }

  /** Returns the sum of the populations of each microbot type. */
  int globalPopulation() {
    return populations.stream().mapToInt(Population::size).sum();
  }

  /** Returns the microbot populations of this snapshot. */
  ImmutableList<Population> populations() {
    return populations;
  }

  /** Returns the microbot populations of this snapshot indexed by population name. */
  ImmutableMap<String, Population> populationsByName() {
    return Maps.uniqueIndex(populations, Population::name);
  }

  /** Returns the time when this snapshot was created, in milliseconds. */
  long creationTimeMillis() {
    return creationTimeMillis;
  }

  /**
   * Returns this snapshot if it is less than the specified age. If it is too old, returns a new
   * snapshot instead.
   */
  PopulationSnapshot refreshIfOlderThan(long ageInMillis) {
    return isOlderThan(ageInMillis) ? refresh() : this;
  }

  /** Returns a new (more recent) snapshot of the same arena as this snapshot. */
  PopulationSnapshot refresh() {
    return of(arena);
  }

  /** Returns whether or not this snapshot is older than the specified age in milliseconds. */
  boolean isOlderThan(long ageInMillis) {
    return System.currentTimeMillis() - creationTimeMillis > ageInMillis;
  }

  /** Returns a new snapshot of the given arena. */
  static PopulationSnapshot of(Arena arena) {
    checkNotNull(arena);

    ImmutableList<Population> populations =
        arena
            .microbots()
            .stream()
            .collect(
                ImmutableSetMultimap.toImmutableSetMultimap(Microbot::name, Function.identity()))
            .asMap()
            .entrySet()
            .stream()
            .map(Population::from)
            .collect(ImmutableList.toImmutableList());
    return new PopulationSnapshot(arena, populations, System.currentTimeMillis());
  }

  /** Represents the population of a specific microbot type at a point in time. */
  static final class Population {

    private final String name;
    private final int size;
    private final Color color;

    private Population(String name, int size, Color color) {
      this.name = name;
      this.size = size;
      this.color = color;
    }

    /** Returns the name of the microbots of this population. */
    String name() {
      return name;
    }

    /** Returns the size of this microbot population. */
    int size() {
      return size;
    }

    /** Returns the color of the microbots of this population. */
    Color color() {
      return color;
    }

    /** Returns a population from the given entry. */
    private static Population from(Map.Entry<String, Collection<Microbot>> entry) {
      return new Population(
          entry.getKey(),
          entry.getValue().size(),
          entry.getValue().stream().findAny().map(Microbot::color).orElse(Color.WHITE));
    }
  }
}
