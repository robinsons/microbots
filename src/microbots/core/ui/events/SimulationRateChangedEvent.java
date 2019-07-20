package microbots.core.ui.events;

import microbots.core.model.SimulationRate;
import microbots.core.util.Event;

/**
 * Event that is posted by an action listener in the {@link microbots.core.ui.WindowMenuBar} each
 * time the user selects a new {@link SimulationRate} from the dropdown menu.
 */
public final class SimulationRateChangedEvent implements Event {
  private final SimulationRate simulationRate;

  public SimulationRateChangedEvent(SimulationRate simulationRate) {
    this.simulationRate = simulationRate;
  }

  public SimulationRate simulationRate() {
    return simulationRate;
  }
}
