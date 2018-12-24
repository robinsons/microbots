package microbots.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ListMultimap;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Represents a snapshot of all microbot populations at a given point in time. */
final class PopulationSnapshot {

  private static final ListMultimap<Arena, PopulationSnapshot> ARENA_SNAPSHOTS =
      ArrayListMultimap.create();

  private final long creationTimeMillis;
  private final ImmutableList<Population> populations;

  private PopulationSnapshot(ImmutableList<Population> populations, long creationTimeMillis) {
    this.populations = populations;
    this.creationTimeMillis = creationTimeMillis;
  }

  /** Returns the microbot populations of this snapshot. */
  ImmutableList<Population> populations() {
    return populations;
  }

  /** Returns the time when this snapshot was created, in milliseconds. */
  long creationTimeMillis() {
    return creationTimeMillis;
  }

  /** Starts a query for a snapshot of the specified arena. */
  static Query of(Arena arena) {
    return new Query(arena);
  }

  /** Allows for filtering a snapshot by some constraints. */
  static final class Query {

    private long maxAgeInMillis = 0L;

    private final Arena arena;

    private Query(Arena arena) {
      this.arena = checkNotNull(arena);
    }

    /**
     * Sets how old the snapshot can be. If a snapshot exists that is not older than the specified
     * age, then it may be returned rather than creating a new snapshot.
     */
    Query withMaxStaleness(long maxAgeInMillis) {
      checkArgument(maxAgeInMillis >= 0L, "maxAgeInMillis must be non-negative.");
      this.maxAgeInMillis = maxAgeInMillis;
      return this;
    }

    /** Fetches a snapshot given the current query's constraints. */
    PopulationSnapshot get() {
      long currentTimeMillis = System.currentTimeMillis();

      synchronized (ARENA_SNAPSHOTS) {
        List<PopulationSnapshot> snapshots = ARENA_SNAPSHOTS.get(arena);

        if (!snapshots.isEmpty()) {
          // Snapshots are stored in order from most to least recent.
          PopulationSnapshot mostRecentSnapshot = snapshots.get(0);
          if (mostRecentSnapshot.creationTimeMillis() + maxAgeInMillis >= currentTimeMillis) {
            return mostRecentSnapshot;
          }
        }

        ImmutableList<Population> populations =
            arena
                .microbots()
                .stream()
                .collect(
                    ImmutableSetMultimap.toImmutableSetMultimap(
                        Microbot::name, Function.identity()))
                .asMap()
                .entrySet()
                .stream()
                .map(Population::from)
                .collect(ImmutableList.toImmutableList());

        PopulationSnapshot snapshot = new PopulationSnapshot(populations, currentTimeMillis);
        snapshots.add(0, snapshot);
        return snapshot;
      }
    }
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
