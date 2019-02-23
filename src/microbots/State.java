package microbots;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Each time a microbot selects an action, it may inspect its present state. The state consists of
 * basic info about the microbot and its surroundings.
 */
public final class State {

  private final Direction facingDirection;
  private final Surroundings surroundings;
  private final Obstacle[] obstacles;

  public State(Direction facingDirection, Surroundings surroundings) {
    this.facingDirection = checkNotNull(facingDirection);
    this.surroundings = checkNotNull(surroundings);

    // Do not change the order of elements of this array.
    this.obstacles =
        new Obstacle[] {
          surroundings.front(), surroundings.right(), surroundings.back(), surroundings.left()
        };
  }

  /** Returns the {@link Direction} that the microbot is facing. */
  public Direction facingDirection() {
    return facingDirection;
  }

  /** Returns the microbot's immediate {@link Surroundings}. */
  public Surroundings surroundings() {
    return surroundings;
  }

  /**
   * Returns the {@link Obstacle} immediately adjacent to the microbot in the specified {@link
   * Direction}.
   */
  public Obstacle obstacleInDirection(Direction direction) {
    // This logic is written with the following assumptions:
    //   * Direction.values() are ordered: NORTH -> EAST -> SOUTH -> WEST
    //   * this.obstacles is ordered: front -> right -> back -> left
    int index =
        (direction.ordinal() - facingDirection.ordinal() + Direction.values().length)
            % Direction.values().length;
    return obstacles[index];
  }
}
