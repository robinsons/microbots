package microbots.core.model.events;

import microbots.core.model.Simulation;
import microbots.core.util.Event;

/** Event that is posted by the running {@link Simulation} each time it completes a round. */
public final class SimulationRoundDoneEvent implements Event {}
