package microbots.core;

/**
 * Represents the four cardinal directions. These are used to track what direction a microbot is
 * facing and to compute cell adjacency in the arena.
 *
 * <p><b>IMPORTANT: do not change the order of elements.</b>
 */
enum Direction {
  NORTH(-1, 0, 0),
  EAST(0, 1, 90),
  SOUTH(1, 0, 180),
  WEST(0, -1, 270);

  static {
    for (Direction direction : values()) {
      if (direction.simpleDirection().ordinal() != direction.ordinal()) {
        throw new RuntimeException(
            "enum microbots.Direction must have the same ordering as Direction.");
      }
    }
  }

  private final int rowOffset;
  private final int columnOffset;
  private final int compassAngleDegrees;

  Direction(int rowOffset, int columnOffset, int compassAngleDegrees) {
    this.rowOffset = rowOffset;
    this.columnOffset = columnOffset;
    this.compassAngleDegrees = compassAngleDegrees;
  }

  /**
   * The microbot battle arena is represented as a 2D grid. Given a cell position {@code (r, c)}, a
   * direction's row and column offsets can be added to the cell coordinates to fetch the next cell
   * in the specified direction, e.g.:
   *
   * <pre>
   *   r += Direction.NORTH.rowOffset();
   *   c += Direction.NORTH.columnOffset();
   * </pre>
   */
  int rowOffset() {
    return rowOffset;
  }

  /** @see #rowOffset() */
  int columnOffset() {
    return columnOffset;
  }

  /** Returns the angle in degrees that this direction would be on a compass, relative to north. */
  int compassAngleDegrees() {
    return compassAngleDegrees;
  }

  /** Returns the angle in radians that this direction would be on a compass, relative to north. */
  double compassAngleRadians() {
    return Math.toRadians(compassAngleDegrees);
  }

  /** Returns the direction that is 90 degrees clockwise relative to {@code this}. */
  Direction clockwise90() {
    return values()[(ordinal() + 1) % 4];
  }

  /** Returns the direction that is 180 degrees clockwise relative to {@code this}. */
  Direction clockwise180() {
    return values()[(ordinal() + 2) % 4];
  }

  /** Returns the direction that is 270 degrees clockwise relative to {@code this}. */
  Direction clockwise270() {
    return values()[(ordinal() + 3) % 4];
  }

  /** Returns the {@link microbots.Direction simple direction} equivalent of {@code this}. */
  microbots.Direction simpleDirection() {
    return microbots.Direction.valueOf(name());
  }

  /** Returns a random direction (selected uniformly). */
  static Direction random() {
    return values()[(int) (4 * Math.random())];
  }
}
