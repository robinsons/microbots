package microbots.impl;

import microbots.Action;
import microbots.MicrobotProcessingUnit;
import microbots.Surroundings;

public final class ScrapPile extends MicrobotProcessingUnit {

  @Override
  public Action getAction(Surroundings surroundings) {
    return Action.WAIT;
  }
}
