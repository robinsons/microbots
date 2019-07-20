package microbots.core.ui;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import microbots.core.Arena;

/**
 * Tracks a sequence of {@link PopulationSnapshot PopulationSnapshots} for monitoring microbot
 * populations over time.
 */
final class PopulationTimeline {

  private final Deque<PopulationSnapshot> snapshots;
  private final long updateFrequencyInMillis;
  private final long maxAgeInMillis;

  private PopulationTimeline(
      Deque<PopulationSnapshot> snapshots, long updateFrequencyInMillis, long maxAgeInMillis) {
    this.snapshots = snapshots;
    this.updateFrequencyInMillis = updateFrequencyInMillis;
    this.maxAgeInMillis = maxAgeInMillis;
  }

  /** Returns the oldest snapshot in the timeline. */
  PopulationSnapshot oldest() {
    return snapshots.getFirst();
  }

  /**
   * Returns the {@link PopulationSnapshot snapshots} in this timeline, sorted from oldest to
   * newest.
   */
  ImmutableList<PopulationSnapshot> snapshots() {
    // Note: must take new snapshot before clearing expired ones, otherwise it is possible to clear
    // all snapshots before a new one can be taken.
    takeNewSnapshotIfNecessary();
    clearExpiredSnapshots();
    return ImmutableList.copyOf(snapshots);
  }

  /** Takes a new snapshot if it has been long enough since the previous snapshot was taken. */
  private void takeNewSnapshotIfNecessary() {
    // Snapshots are stored from oldest to newest, so we only need to check if the last element of
    // the deque is older than this timeline's update frequency.
    PopulationSnapshot mostRecentSnapshot = snapshots.getLast();
    if (mostRecentSnapshot.isOlderThan(updateFrequencyInMillis)) {
      snapshots.addLast(mostRecentSnapshot.refresh());
    }
  }

  /** Removes any snapshots that are past the max age of this timeline. */
  private void clearExpiredSnapshots() {
    // Snapshots are stored from oldest to newest, so we can remove snapshots from the front of the
    // deque until we either:
    //    (1) Run out of snapshots; or,
    //    (2) come across a snapshot that is not expired.
    Iterator<PopulationSnapshot> iterator = snapshots.iterator();
    while (iterator.hasNext() && iterator.next().isOlderThan(maxAgeInMillis)) {
      iterator.remove();
    }
  }

  /** Returns a new {@link FrequencySpec} for the given arena. */
  static FrequencySpec snapshot(Arena arena) {
    return new Builder(arena);
  }

  /** Builder interface for specifying the update frequency of a timeline. */
  interface FrequencySpec {
    /**
     * Returns a new {@link RetentionSpec} based on this spec's parameters. Specifies that the
     * resulting timeline should take a new snapshot at the specified interval. Note that snapshots
     * are only taken on calls to {@link PopulationTimeline#snapshots()}, and so this value only
     * configures a lower bound for how frequently snapshots are taken.
     */
    RetentionSpec every(long updateFrequencyInMillis);

    /**
     * Returns a new {@link RetentionSpec} based on this spec's parameters. Specifies that the
     * resulting timeline should take a new snapshot each time {@link
     * PopulationTimeline#snapshots()} is queried.
     */
    default RetentionSpec onEveryQuery() {
      return every(1L);
    }
  }

  /** Builder for specifying the max age of snapshots in a timeline. */
  interface RetentionSpec {
    /**
     * Returns a new timeline from the parameters of this spec. The timeline will retain snapshots
     * forever.
     */
    PopulationTimeline retainForever();

    /**
     * Returns a new timeline from the parameters of this spec. The timeline will retain snapshots
     * up to the specified {@code maxAgeInMillis}.
     */
    PopulationTimeline retainFor(long maxAgeInMillis);
  }

  /** Builder for specifying the update frequency of the timeline. */
  static final class Builder implements FrequencySpec, RetentionSpec {

    private long updateFrequencyInMillis;

    private final Arena arena;

    private Builder(Arena arena) {
      this.arena = checkNotNull(arena);
    }

    @Override
    public RetentionSpec every(long updateFrequencyInMillis) {
      checkArgument(updateFrequencyInMillis > 0, "updateFrequencyInMillis must be positive.");
      this.updateFrequencyInMillis = updateFrequencyInMillis;
      return this;
    }

    @Override
    public PopulationTimeline retainForever() {
      return retainFor(Long.MAX_VALUE);
    }

    @Override
    public PopulationTimeline retainFor(long maxAgeInMillis) {
      checkArgument(maxAgeInMillis > 0, "maxAgeInMillis must be positive.");
      checkArgument(
          updateFrequencyInMillis <= maxAgeInMillis,
          "Snapshot update frequency cannot be greater than max snapshot age.");

      Deque<PopulationSnapshot> snapshots = new ArrayDeque<>();
      snapshots.addLast(PopulationSnapshot.of(arena));
      return new PopulationTimeline(snapshots, updateFrequencyInMillis, maxAgeInMillis);
    }
  }
}
