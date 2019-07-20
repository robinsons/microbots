package microbots.core.util;

import com.google.common.eventbus.EventBus;
import microbots.core.model.Simulation;
import microbots.core.model.SimulationRate;
import microbots.core.ui.Window;

/** Utility class to coordinate events between the model and UI via an {@link EventBus}. */
public final class Events {

  private static final EventBus EVENT_BUS = new EventBus();

  // Not intended for instantiation.
  private Events() {}

  /** @see EventBus#register(Object) */
  public static void register(Object object) {
    EVENT_BUS.register(object);
  }

  /** @see EventBus#unregister(Object) */
  public static void unregister(Object object) {
    EVENT_BUS.unregister(object);
  }

  /** @see EventBus#post(Object) */
  public static void post(Event event) {
    EVENT_BUS.post(event);
  }

  /** Event that is posted by the running {@link Simulation} each time it completes a round. */
  public static final class SimulationRoundDoneEvent implements Event {
  }

  /**
   * Event that is posted by a {@link Simulation} when {@link Simulation#run()} is called on that
   * instance.
   */
  public static final class SimulationRunCalledEvent implements Event {
    private final Simulation simulation;

    public SimulationRunCalledEvent(Simulation simulation) {
      this.simulation = simulation;
    }

    public Simulation simulation() {
      return simulation;
    }
  }

}
