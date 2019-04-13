package microbots.impl;

import java.awt.Color;
import microbots.Action;
import microbots.MicrobotProcessingUnit;
import microbots.Obstacle;
import microbots.State;
import microbots.Surroundings;

public final class Hive extends MicrobotProcessingUnit {
  @Override
  public Color color() {
    return new Color(0xf7fc94);
  }

  @Override
  public Action getAction(State state) {
    Surroundings surroundings = state.surroundings();

    if (surroundings.front() == Obstacle.ENEMY) {
      return Action.HACK;
    }

    if (surroundings.left() == Obstacle.ENEMY) {
      return Action.ROTATE_LEFT;
    }
    if (surroundings.right() == Obstacle.ENEMY) {
      return Action.ROTATE_RIGHT;
    }

    if (surroundings.back() == Obstacle.FRIEND) {
      return Action.WAIT;
    }

    if (surroundings.front() == Obstacle.NONE) {
      return Action.MOVE;
    }

    if (surroundings.left() == Obstacle.NONE) {
      return Action.ROTATE_LEFT;
    } else {
      return Action.ROTATE_RIGHT;
    }
  }
}
