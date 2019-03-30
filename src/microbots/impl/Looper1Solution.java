package microbots.impl;

import java.awt.Color;
import microbots.Action;
import microbots.MicrobotProcessingUnit;
import microbots.State;

/**
 * Challenge: Create a bot that moves in a "circle" fashion. It should move one cell north, then one
 * cell east, then one cell south, then one cell west, and repeat.
 */
public final class Looper1Solution extends MicrobotProcessingUnit {

  private static final Action[] ACTIONS = new Action[] {Action.MOVE, Action.ROTATE_RIGHT};
  private int actionIndex = -1;

  @Override
  public Color color() {
    return new Color(0xdaed4b);
  }

  @Override
  public Action getAction(State state) {
    actionIndex = (actionIndex + 1) % ACTIONS.length;
    return ACTIONS[actionIndex];
  }
}
