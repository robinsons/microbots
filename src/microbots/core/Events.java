package microbots.core;

import com.google.common.eventbus.EventBus;

/**
 * Holds various {@link EventBus} publishers that can be used for communication between the UI and
 * the data model.
 */
final class Events {

  // TODO: change this to use just one EventBus but post different event types.
  static final EventBus WINDOW_REPAINT_DONE = new EventBus("WindowRepaintDone");
  static final EventBus SIMULATION_ROUND_DONE = new EventBus("SimulationRoundDone");
  static final EventBus SIMULATION_BUILD_NEW_CALLED = new EventBus("SimulationBuildNewCalled");
  static final EventBus SIMULATION_RATE_CHANGED = new EventBus("SimulationRateChanged");

  // Not intended for instantiation.
  private Events() {}
}
