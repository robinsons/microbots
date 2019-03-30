package microbots.impl;

import java.awt.Color;
import microbots.Action;
import microbots.Direction;
import microbots.MicrobotProcessingUnit;
import microbots.State;

/**
 * Challenge: After completing {@link Looper1Solution}, create a bot that moves in a similar
 * fashion, but the radius of its spiral increases by 1 each time it completes a revolution. In
 * other words, after completing the motion described in Looper1Solution, it should then move 2
 * cells in each direction, then 3, and so on, increasing by 1 each time it completes a revolution.
 */
public final class Looper2Solution extends MicrobotProcessingUnit {

  private Direction startingDirection;
  private int movesToMake = 1;
  private int movesMade = 0;

  @Override
  public Color color() {
    return new Color(0x64c466);
  }

  @Override
  public Action getAction(State state) {
    if (startingDirection == null) {
      startingDirection = state.facingDirection();
    }

    if (movesMade < movesToMake) {
      movesMade++;
      return Action.MOVE;
    }

    if (isCounterclockwiseFromStartingDirection(state.facingDirection())) {
      movesToMake++;
    }

    movesMade = 0;
    return Action.ROTATE_RIGHT;
  }

  private boolean isCounterclockwiseFromStartingDirection(Direction direction) {
    switch (startingDirection) {
      case NORTH:
        return direction == Direction.WEST;
      case EAST:
        return direction == Direction.NORTH;
      case SOUTH:
        return direction == Direction.EAST;
      case WEST:
        return direction == Direction.SOUTH;
    }
    return false;
  }
}
