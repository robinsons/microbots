package microbots.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import java.util.Optional;
import microbots.Obstacle;
import microbots.Surroundings;

/** The arena is where microbots do battle. */
final class Arena {

  private final Table<Integer, Integer, Microbot> grid;
  private final int rows;
  private final int columns;

  private Arena(Table<Integer, Integer, Microbot> grid, int rows, int columns) {
    this.grid = grid;
    this.rows = rows;
    this.columns = columns;
  }

  /**
   * Returns the surroundings of the given microbot. A microbot's surroundings are the four cells
   * immediately adjacent to that microbot in the cardinal {@link Direction directions}. The
   * returned surroundings are oriented relative to the direction the microbot is facing, i.e. they
   * use the terms "front" and "back" rather than "north" and "south".
   */
  Surroundings getMicrobotSurroundings(Microbot microbot) {
    return new Surroundings(
        getObstacleRelativeToMicrobot(microbot, microbot.facing()),
        getObstacleRelativeToMicrobot(microbot, microbot.facing().previous()),
        getObstacleRelativeToMicrobot(microbot, microbot.facing().next()),
        getObstacleRelativeToMicrobot(microbot, microbot.facing().opposite()));
  }

  /** Returns the obstacle in the given direction relative to the indicated microbot. */
  private Obstacle getObstacleRelativeToMicrobot(Microbot microbot, Direction direction) {
    int otherRow = microbot.row() + direction.rowOffset();
    int otherColumn = microbot.column() + direction.columnOffset();

    if (!inBounds(otherRow, otherColumn)) {
      return Obstacle.WALL;
    }

    Optional<Microbot> otherMicrobot = Optional.ofNullable(grid.get(otherRow, otherColumn));
    return otherMicrobot.map(microbot::classify).orElse(Obstacle.NONE);
  }

  /**
   * Returns whether or not the indicated row and column positions are within the bounds of this
   * arena.
   */
  private boolean inBounds(int row, int column) {
    return 0 <= row && row < rows && 0 <= column && column < columns;
  }

  /** Returns a new {@link Builder} for constructing arenas. */
  static Builder builder() {
    return new Builder();
  }

  /** Builder class for creating arena instances. */
  static final class Builder {

    private int rows = 75;
    private int columns = 100;
    private ImmutableList<Microbot> microbots = ImmutableList.of();

    /** Sets the number of rows in the arena. Must be positive. */
    public Builder withRows(int rows) {
      checkArgument(rows > 0, "rows must be positive.");
      this.rows = rows;
      return this;
    }

    /** Sets the number of columns in the arena. Must be positive. */
    public Builder withColumns(int columns) {
      checkArgument(columns > 0, "columns must be positive.");
      this.columns = columns;
      return this;
    }

    /** Sets the microbots that will participate in the simulation. */
    public Builder withMicrobots(ImmutableList<Microbot> microbots) {
      this.microbots = checkNotNull(microbots);
      return this;
    }

    /** Returns a new arena instance. Can only be called once. */
    public Arena build() {
      checkArgument(
          rows * columns >= microbots.size(),
          "Arena of size %d is not large enough to accommodate %d microbots.",
          rows * columns,
          microbots.size());

      Table<Integer, Integer, Microbot> grid = HashBasedTable.create();
      microbots.forEach(microbot -> placeMicrobot(microbot, grid));
      return new Arena(grid, rows, columns);
    }

    /**
     * Places the given microbot in a random location on the grid. The first microbot is always
     * placed at (0, 0).
     */
    private void placeMicrobot(Microbot microbot, Table<Integer, Integer, Microbot> grid) {
      int row = 0;
      int column = 0;
      while (grid.contains(row, column)) {
        row = (int)(rows * Math.random());
        column = (int)(columns * Math.random());
      }

      microbot.setRow(row);
      microbot.setColumn(column);
      grid.put(row, column, microbot);
    }
  }
}
