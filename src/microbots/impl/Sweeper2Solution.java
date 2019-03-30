package microbots.impl;

import java.awt.Color;
import microbots.Action;
import microbots.Direction;
import microbots.MicrobotProcessingUnit;
import microbots.Obstacle;
import microbots.State;

/**
 * Challenge: After completing {@link Sweeper1Solution}, create a bot that moves in a similar
 * fashion, but when it encounters a friend, it moves one cell to the side before continuing in the
 * direction it was traveling.
 */
public final class Sweeper2Solution extends MicrobotProcessingUnit {

  private Direction targetDirection = Direction.EAST;

  @Override
  public Color color() {
    return new Color(0xbc54bc);
  }

  @Override
  public Action getAction(State state) {
    if (state.facingDirection() != targetDirection) {
      if (state.obstacleInDirection(targetDirection) == Obstacle.FRIEND) {
        return Action.MOVE;
      } else {
        return Action.ROTATE_RIGHT;
      }
    }

    if (state.surroundings().front() == Obstacle.NONE) {
      return Action.MOVE;
    }

    if (state.surroundings().front() == Obstacle.FRIEND) {
      return Action.ROTATE_LEFT;
    }

    targetDirection = nextDirection();
    return Action.ROTATE_RIGHT;
  }

  private Direction nextDirection() {
    if (targetDirection == Direction.EAST) {
      return Direction.WEST;
    } else {
      return Direction.EAST;
    }
  }
}
