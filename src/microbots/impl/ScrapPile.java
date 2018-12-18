package microbots.impl;

import java.awt.Color;
import microbots.Action;
import microbots.MicrobotProcessingUnit;
import microbots.Surroundings;

public final class ScrapPile extends MicrobotProcessingUnit {

  private static final Color COLOR = new Color(0x4286f4);

  @Override
  public Color color() {
    return COLOR;
  }

  @Override
  public Action getAction(Surroundings surroundings) {
    return Action.WAIT;
  }
}
