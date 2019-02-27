package microbots.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.Subscribe;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.BiConsumer;
import microbots.Action;
import microbots.MicrobotProcessingUnit;
import microbots.State;
import microbots.Surroundings;

/**
 * Entry point for configuring a microbot battle simulation. Example usage:
 *
 * <pre>
 *   static final class Microbot9000 extends MicrobotProcesingUnit { ... }
 *   static final class MicrobotPrime extends MicrobotProcesingUnit { ... }
 *   ...
 *   Simulation.builder()
 *       .setPopulationSize(500)
 *       .addMpuType(Microbot9000.class)
 *       .addMpuType(MicrobotPrime.class)
 *       .start();
 * </pre>
 */
public final class Simulation {

  /** Simple functional interface to provide type clarity for the {@link #ACTION_DELEGATES} map. */
  @FunctionalInterface
  private interface ActionDelegate extends BiConsumer<Simulation, Microbot> {}

  /**
   * Maps {@link Action microbot actions} to {@link ActionDelegate delegates} for handling each
   * action. Using a map allows us to avoid a large switch statement.
   */
  private static final ImmutableMap<Action, ActionDelegate> ACTION_DELEGATES =
      ImmutableMap.of(
          Action.WAIT, Simulation::handleWait,
          Action.MOVE, Simulation::handleMove,
          Action.ROTATE_LEFT, Simulation::handleRotateLeft,
          Action.ROTATE_RIGHT, Simulation::handleRotateRight,
          Action.HACK, Simulation::handleHack);

  private boolean terminationRequested = false;
  private boolean windowRepaintDoneCalled = false;

  private final ImmutableList<Microbot> microbots;
  private final Arena arena;
  private SimulationRate simulationRate;

  private Simulation(
      ImmutableList<Microbot> microbots, Arena arena, SimulationRate simulationRate) {
    this.microbots = microbots;
    this.arena = arena;
    this.simulationRate = simulationRate;
  }

  /** Returns the list of {@link Microbot Microbots} participating in this {@link Simulation}. */
  ImmutableList<Microbot> microbots() {
    return microbots;
  }

  /** Returns the {@link Arena} of this {@link Simulation}. */
  Arena arena() {
    return arena;
  }

  /** Callback for {@link Events#SIMULATION_BUILD_NEW_CALLED}. */
  @Subscribe
  public void onSimulationBuildNewCalled(Simulation simulation) {
    terminationRequested = this != simulation;
  }

  /** Callback for {@link Events#SIMULATION_RATE_CHANGED}. */
  @Subscribe
  public void onSimulationRateChanged(SimulationRate simulationRate) {
    this.simulationRate = simulationRate;
  }

  /** Callback for {@link Events#WINDOW_REPAINT_DONE}. */
  @Subscribe
  public void onWindowRepaintDone(String ignored) {
    windowRepaintDoneCalled = true;
  }

  /** Runs the simulation! */
  void run() throws Exception {
    while (!terminationRequested) {
      doRound();

      windowRepaintDoneCalled = false;
      Events.SIMULATION_ROUND_DONE.post("foo");
      do {
        Thread.sleep(simulationRate.millisPerRound());
      } while (!windowRepaintDoneCalled);
    }

    Events.WINDOW_REPAINT_DONE.unregister(this);
    Events.SIMULATION_BUILD_NEW_CALLED.unregister(this);
    Events.SIMULATION_RATE_CHANGED.unregister(this);
  }

  /**
   * Performs a single round of the simulation. In each round, every microbot in the simulation gets
   * to perform one action.
   */
  void doRound() {
    microbots.forEach(this::processAction);
  }

  /** Performs a single action for the specified microbot. */
  private void processAction(Microbot microbot) {
    Surroundings surroundings = arena.getMicrobotSurroundings(microbot);
    State state = new State(microbot.facing().simpleDirection(), surroundings);
    Action action = microbot.getAction(state);
    ActionDelegate delegate =
        ACTION_DELEGATES.getOrDefault(action, Simulation::handleUnknownAction);

    delegate.accept(this, microbot);
  }

  /** Delegate for {@link Action#WAIT}. */
  private void handleWait(Microbot microbot) {
    // Do nothing!
  }

  /** Delegate for {@link Action#MOVE}. */
  private void handleMove(Microbot microbot) {
    arena.moveMicrobot(microbot);
  }

  /** Delegate for {@link Action#ROTATE_LEFT}. */
  private void handleRotateLeft(Microbot microbot) {
    microbot.rotateLeft();
  }

  /** Delegate for {@link Action#ROTATE_RIGHT}. */
  private void handleRotateRight(Microbot microbot) {
    microbot.rotateRight();
  }

  /** Delegate for {@link Action#HACK}. */
  private void handleHack(Microbot microbot) {
    Optional<Microbot> other = arena.getFacedMicrobot(microbot);
    other.ifPresent(microbot::hack);
  }

  /** Fallback delegate for actions that aren't handled. */
  private void handleUnknownAction(Microbot microbot) {
    throw new RuntimeException("Action not handled!");
  }

  /** Returns a new {@link Builder} for constructing simulation instances. */
  public static Builder builder() {
    return new Builder()
        .setPopulationSize(SimulationDefaults.POPULATION_SIZE)
        .setArenaMap(SimulationDefaults.ARENA_MAP)
        .setSimulationRate(SimulationDefaults.SIMULATION_RATE);
  }

  /** Builder class for constructing simulation instances. */
  public static final class Builder {

    private int populationSize;
    private ArenaMap arenaMap;
    private SimulationRate simulationRate;
    private final HashSet<Class<? extends MicrobotProcessingUnit>> mpuTypes = new HashSet<>();

    // PUBLIC API

    /** Adds a new {@link MicrobotProcessingUnit MPU type} to be included in the simulation. */
    public Builder addMpuType(Class<? extends MicrobotProcessingUnit> mpuType) {
      checkNotNull(mpuType);
      mpuTypes.add(mpuType);
      return this;
    }

    /**
     * Builds a simulation based on the parameters of this builder, and then starts it in a new
     * window.
     */
    public void start() throws Exception {
      Window.create();
      build().run();
    }

    // PUBLIC API ENDS HERE. Below this point is the internal API.

    /**
     * Adds each {@link MicrobotProcessingUnit MPU type} from the given collection to the simulation
     * being built.
     */
    Builder addMpuTypes(ImmutableCollection<Class<? extends MicrobotProcessingUnit>> mpuTypes) {
      checkNotNull(mpuTypes);
      mpuTypes.forEach(this::addMpuType);
      return this;
    }

    /**
     * Sets the population size, which is the number of microbots of each type that will be included
     * in the simulation. Must be positive.
     */
    Builder setPopulationSize(int populationSize) {
      checkArgument(populationSize > 0, "populationSize must be positive.");
      this.populationSize = populationSize;
      return this;
    }

    /** Sets the arena map to use in the simulation. */
    Builder setArenaMap(ArenaMap arenaMap) {
      this.arenaMap = checkNotNull(arenaMap);
      return this;
    }

    /** Sets the simulation rate to use in the new simulation. */
    Builder setSimulationRate(SimulationRate simulationRate) {
      this.simulationRate = checkNotNull(simulationRate);
      return this;
    }

    /** Builds a simulation based on the parameters of this builder, but does not start it. */
    Simulation build() throws Exception {
      ImmutableList<Microbot> microbots = MicrobotFactory.create(populationSize).ofEach(mpuTypes);
      Arena arena = Arena.builder().withMap(arenaMap).withMicrobots(microbots).build();
      Simulation simulation = new Simulation(microbots, arena, simulationRate);

      Events.WINDOW_REPAINT_DONE.register(simulation);
      Events.SIMULATION_BUILD_NEW_CALLED.register(simulation);
      Events.SIMULATION_RATE_CHANGED.register(simulation);

      // Existing simulations will be registered for this event. By comparing themselves to the
      // passed simulation, they will take this as a signal to break out of their run() loops.
      Events.SIMULATION_BUILD_NEW_CALLED.post(simulation);

      return simulation;
    }
  }
}
