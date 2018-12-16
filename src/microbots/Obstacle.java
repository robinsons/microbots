package microbots;

/**
 * Microbot battles take place on a 2D grid. An obstacle represents the possible states of a single
 * cell in that grid.
 */
public enum Obstacle {

  /**
   * Indicates that a cell has nothing in it. This is the only state that permits movement into this
   * cell.
   */
  NONE,

  /** Indicates that a cell has a wall and is therefore impassable. */
  WALL,

  /**
   * Indicates that a cell is occupied by a friendly microbot. The cell is impassable and not
   * hackable.
   */
  FRIEND,

  /**
   * Indicates that a cell is occupied by an enemy microbot. The cell is impassable but may be
   * hacked.
   */
  ENEMY
}
