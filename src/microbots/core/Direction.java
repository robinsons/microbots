package microbots.core;

/**
 * Represents the four cardinal directions. These are used to track what direction a microbot is
 * facing and to compute cell adjacency in the arena.
 *
 * <p><b>IMPORTANT: do not change the order of elements.</b>
 */
enum Direction {
  NORTH(-1, 0),
  EAST(0, 1),
  SOUTH(1, 0),
  WEST(0, -1);

  private final int rowOffset;
  private final int columnOffset;

  Direction(int rowOffset, int columnOffset) {
    this.rowOffset = rowOffset;
    this.columnOffset = columnOffset;
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

  /** Returns a random direction (selected uniformly). */
  static Direction random() {
    return values()[(int) (4 * Math.random())];
  }
}
