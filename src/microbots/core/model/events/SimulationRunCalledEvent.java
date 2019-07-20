package microbots.core.model.events;

import microbots.core.model.Simulation;
import microbots.core.util.Event;

/**
 * Event that is posted by a {@link Simulation} when {@link Simulation#run()} is called on that
 * instance.
 */
public final class SimulationRunCalledEvent implements Event {
  private final Simulation simulation;

  public SimulationRunCalledEvent(Simulation simulation) {
    this.simulation = simulation;
  }

  public Simulation simulation() {
    return simulation;
  }
}
