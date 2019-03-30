package microbots.impl;

import java.awt.Color;
import microbots.Action;
import microbots.Direction;
import microbots.MicrobotProcessingUnit;
import microbots.Obstacle;
import microbots.State;

/**
 * Challenge: Create a bot that moves east as much as possible, then rotates 180 degrees and moves
 * west as much as possible, and then repeats.
 */
public final class Sweeper1Solution extends MicrobotProcessingUnit {

  private Direction targetDirection = Direction.EAST;

  @Override
  public Color color() {
    return new Color(0x2b6dd8);
  }

  @Override
  public Action getAction(State state) {
    if (state.facingDirection() != targetDirection) {
      return Action.ROTATE_RIGHT;
    }

    if (state.surroundings().front() == Obstacle.NONE) {
      return Action.MOVE;
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
