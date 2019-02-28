package microbots.core;

import com.google.common.eventbus.EventBus;

/** Utility class to coordinate events between the model and UI via an {@link EventBus}. */
final class Events {

  private static final EventBus EVENT_BUS = new EventBus();

  // Not intended for instantiation.
  private Events() {}

  /** @see EventBus#register(Object) */
  static void register(Object object) {
    EVENT_BUS.register(object);
  }

  /** @see EventBus#unregister(Object) */
  static void unregister(Object object) {
    EVENT_BUS.unregister(object);
  }

  /** @see EventBus#post(Object) */
  static void post(Event event) {
    EVENT_BUS.post(event);
  }

  /** Events are used to pass state or notifications between the model and the UI. */
  interface Event {}

  /** Event that is posted by the {@link Window} each time it finishes repainting itself. */
  static final class WindowRepaintDoneEvent implements Event {}

  /** Event that is posted by the running {@link Simulation} each time it completes a round. */
  static final class SimulationRoundDoneEvent implements Event {}

  /**
   * Event that is posted by a {@link Simulation} when {@link Simulation#run()} is called on that
   * instance.
   */
  static final class SimulationRunCalledEvent implements Event {
    private final Simulation simulation;

    SimulationRunCalledEvent(Simulation simulation) {
      this.simulation = simulation;
    }

    Simulation simulation() {
      return simulation;
    }
  }

  /**
   * Event that is posted by an action listener in the {@link WindowMenuBar} each time the user
   * selects a new {@link SimulationRate} from the dropdown menu.
   */
  static final class SimulationRateChangedEvent implements Event {
    private final SimulationRate simulationRate;

    SimulationRateChangedEvent(SimulationRate simulationRate) {
      this.simulationRate = simulationRate;
    }

    SimulationRate simulationRate() {
      return simulationRate;
    }
  }
}
