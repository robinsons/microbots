package microbots.core;

/**
 * Represents the four cardinal directions. In the microbot arena, these are used to compute cell
 * adjacency.
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
   * The microbot battle arena is represented as a 2D grid (matrix). Given a cell position {@code
   * (r, c)}, a direction's row and column offsets can be added to the cell coordinates to fetch the
   * next cell in the specified direction, e.g.:
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

  /**
   * Returns the next direction after {@code this} in the {@link Direction} enum. The order of
   * elements in this enum has been defined in such a way that this is equivalent to performing a 90
   * degree clockwise rotation.
   */
  Direction next() {
    return values()[(ordinal() + 1) % 4];
  }

  /**
   * Similar to {@link #next()}, but returns the previous element instead. This is equivalent to
   * performing a 90 degree counterclockwise rotation.
   */
  Direction previous() {
    // Why "ordinal() + 3" instead of "ordinal() - 1"? The modulo operator in Java preserves the
    // sign of the leading operand. In the case where ordinal() == 0, computing the previous index
    // would then result in an ArrayIndexOutOfBoundsException (-1). Thus, to ensure that we always
    // have a non-negative index, we add 3 which is equivalent to -1 modulo 4.
    return values()[(ordinal() + 3) % 4];
  }

  /**
   * Similar to {@link #next()} or {@link #previous()}, but returns the element opposite {@code
   * this} instead. This is equivalent to performing a 180 degree rotation.
   */
  Direction opposite() {
    return values()[(ordinal() + 2) % 4];
  }

  /** Returns a random direction (selected uniformly). */
  static Direction random() {
    return values()[(int) (4 * Math.random())];
  }
}
