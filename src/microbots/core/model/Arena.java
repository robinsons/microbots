package microbots.core.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import java.util.Optional;
import microbots.Obstacle;
import microbots.Surroundings;

/** The arena is where microbots do battle. */
public final class Arena {

  private final Table<Integer, Integer, Microbot> microbots;
  private final ImmutableTable<Integer, Integer, Terrain> terrain;
  private final ArenaMap arenaMap;

  private Arena(
      Table<Integer, Integer, Microbot> microbots,
      ImmutableTable<Integer, Integer, Terrain> terrain,
      ArenaMap arenaMap) {
    this.microbots = microbots;
    this.terrain = terrain;
    this.arenaMap = arenaMap;
  }

  /** Returns the number of rows in this arena. */
  public int rows() {
    return arenaMap.rows();
  }

  /** Returns the number of columns in this arena. */
  public int columns() {
    return arenaMap.columns();
  }

  /** Returns all of the microbots currently in this arena. */
  public ImmutableSet<Microbot> microbots() {
    synchronized (microbots) {
      return ImmutableSet.copyOf(microbots.values());
    }
  }

  /** Returns this arena's terrain. */
  public ImmutableTable<Integer, Integer, Terrain> terrain() {
    return terrain;
  }

  /**
   * Returns the microbot in the adjacent cell in the direction that the given microbot is facing,
   * or else {@link Optional#empty()} if that cell is unoccupied.
   */
  Optional<Microbot> getFacedMicrobot(Microbot microbot) {
    Direction direction = microbot.facing();
    return microbotAt(
        microbot.row() + direction.rowOffset(), microbot.column() + direction.columnOffset());
  }

  /**
   * Moves the given microbot one cell in the direction it is currently facing, provided that the
   * destination cell is unoccupied and is within the bounds of the arena.
   */
  void moveMicrobot(Microbot microbot) {
    checkNotNull(microbot);
    Direction direction = microbot.facing();
    synchronized (microbots) {
      if (getObstacleRelativeToMicrobot(microbot, direction) == Obstacle.NONE) {
        microbots.remove(microbot.row(), microbot.column());
        microbot.setPosition(
            normalizeRow(microbot.row() + direction.rowOffset()),
            normalizeColumn(microbot.column() + direction.columnOffset()));
        microbots.put(microbot.row(), microbot.column(), microbot);
      }
    }
  }

  /**
   * Returns the surroundings of the given microbot. A microbot's surroundings are the four cells
   * immediately adjacent to that microbot in the cardinal {@link Direction directions}. The
   * returned surroundings are oriented relative to the direction the microbot is facing, i.e. they
   * use the terms "front" and "back" rather than "north" and "south".
   */
  Surroundings getMicrobotSurroundings(Microbot microbot) {
    checkNotNull(microbot);
    return new Surroundings(
        getObstacleRelativeToMicrobot(microbot, microbot.facing()), // front
        getObstacleRelativeToMicrobot(microbot, microbot.facing().clockwise270()), // left
        getObstacleRelativeToMicrobot(microbot, microbot.facing().clockwise90()), // right
        getObstacleRelativeToMicrobot(microbot, microbot.facing().clockwise180())); // back
  }

  /** Returns the obstacle in the given direction relative to the indicated microbot. */
  private Obstacle getObstacleRelativeToMicrobot(Microbot microbot, Direction direction) {
    int otherRow = microbot.row() + direction.rowOffset();
    int otherColumn = microbot.column() + direction.columnOffset();

    if (!terrainAt(otherRow, otherColumn).isTraversable()) {
      return Obstacle.WALL;
    }

    return microbotAt(otherRow, otherColumn).map(microbot::classify).orElse(Obstacle.NONE);
  }

  /**
   * Returns the microbot located at the specified position, or else {@link Optional#empty()} if the
   * position is unoccupied.
   */
  private Optional<Microbot> microbotAt(int row, int column) {
    return Optional.ofNullable(microbots.get(normalizeRow(row), normalizeColumn(column)));
  }

  /** Returns the terrain located at the specified position. */
  private Terrain terrainAt(int row, int column) {
    return terrain.get(normalizeRow(row), normalizeColumn(column));
  }

  /**
   * Normalizes the given row so that it is guaranteed to be in the bounds of this arena. This works
   * as long as the given row is in the interval {@code [-rows(),Integer.MAX_VALUE]}.
   */
  private int normalizeRow(int row) {
    return (row + rows()) % rows();
  }

  /**
   * Normalizes the given column so that it is guaranteed to be in the bounds of this arena. This
   * works as long as the given column is in the interval {@code [-columns(),Integer.MAX_VALUE]}.
   */
  private int normalizeColumn(int column) {
    return (column + columns()) % columns();
  }

  /** Returns a new {@link Builder} for constructing arenas. */
  static Builder builder() {
    return new Builder();
  }

  /** Builder class for creating arena instances. */
  static final class Builder {

    private ArenaMap map;
    private ImmutableList<Microbot> microbots = ImmutableList.of();

    Builder withMap(ArenaMap map) {
      this.map = checkNotNull(map);
      return this;
    }

    /** Sets the microbots that will participate in the simulation. */
    Builder withMicrobots(ImmutableList<Microbot> microbots) {
      this.microbots = checkNotNull(microbots);
      return this;
    }

    /** Returns a new arena instance. */
    Arena build() {
      checkNotNull(map);
      checkArgument(
          map.traversableSpaceCount() >= microbots.size(),
          "Arena has only %d traversable spaces, which is not enough to accommodate %d microbots.",
          map.traversableSpaceCount(),
          microbots.size());

      Table<Integer, Integer, Microbot> grid = HashBasedTable.create();
      ImmutableTable<Integer, Integer, Terrain> terrain = map.terrain();
      microbots.forEach(microbot -> placeMicrobot(microbot, grid, terrain));

      return new Arena(grid, terrain, map);
    }

    /**
     * Places the given microbot in a random location on the grid. All microbots are placed in a
     * position such that the corresponding location in the terrain is {@link
     * Terrain#isTraversable() traversable}.
     */
    private void placeMicrobot(
        Microbot microbot,
        Table<Integer, Integer, Microbot> grid,
        Table<Integer, Integer, Terrain> terrain) {
      int row = 0;
      int column = 0;

      // This approach becomes inefficient as the ratio of microbots to arena cells approaches 1.
      // Consider refactoring if arenas are not sparsely populated.
      while (grid.contains(row, column) || !terrain.get(row, column).isTraversable()) {
        row = (int) (map.rows() * Math.random());
        column = (int) (map.columns() * Math.random());
      }

      microbot.setPosition(row, column);
      grid.put(row, column, microbot);
    }
  }
}
