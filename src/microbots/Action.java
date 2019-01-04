package microbots;

/**
 * The things a microbot may attempt to do on their turn. An action may not always succeed: for
 * example, a microbot that attempts to move into a wall will simply stay put. A microbot must select
 * exactly one action to perform on their turn. If the action fails (e.g. because it was invalid),
 * then the microbot is <b>not</b> given the opportunity to select another.
 */
public enum Action {

  /** The microbot does nothing for this turn. This action always succeeds. */
  WAIT,

  /**
   * The microbot attempts to move into the cell directly in front of them. If that cell is
   * impassable (read about the various {@link Obstacle Obstacles} to determine this), then nothing
   * happens.
   */
  MOVE,

  /** The microbot rotates 90 degrees counterclockwise. This action always succeeds. */
  ROTATE_LEFT,

  /** The microbot rotates 90 degrees clockwise. This action always succeeds. */
  ROTATE_RIGHT,

  /**
   * The microbot attempts to hack the enemy microbot residing in the cell directly in front of it.
   * If there is an enemy, then it is converted to the same type of microbot as the one who
   * performed the hacking. If not, then nothing happens.
   */
  HACK
}
