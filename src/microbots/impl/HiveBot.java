package microbots.impl;

import java.awt.Color;
import microbots.Action;
import microbots.MicrobotProcessingUnit;
import microbots.Obstacle;
import microbots.State;
import microbots.Surroundings;

/** Example MPU implementation. The HiveBot tends to form clusters. */
public final class HiveBot extends MicrobotProcessingUnit {

  private static final Color COLOR = new Color(0xeef442);

  @Override
  public Color color() {
    return COLOR;
  }

  @Override
  public Action getAction(State state) {
    Surroundings surroundings = state.surroundings();

    // Always hack if it is possible.
    if (surroundings.front() == Obstacle.ENEMY) {
      return Action.HACK;
    }

    // Turn towards enemies to prepare for hacking.
    if (surroundings.left() == Obstacle.ENEMY) {
      return Action.ROTATE_LEFT;
    }
    if (surroundings.right() == Obstacle.ENEMY) {
      return Action.ROTATE_RIGHT;
    }

    // The back is vulnerable, so stay near an ally if they are behind you.
    if (surroundings.back() == Obstacle.FRIEND) {
      return Action.WAIT;
    }

    // Move forward if possible.
    if (surroundings.front() == Obstacle.NONE) {
      return Action.MOVE;
    }

    // Set up a movement on a subsequent turn by trying to face a direction that is clear.
    if (surroundings.left() == Obstacle.NONE) {
      return Action.ROTATE_LEFT;
    } else {
      return Action.ROTATE_RIGHT;
    }
  }
}
