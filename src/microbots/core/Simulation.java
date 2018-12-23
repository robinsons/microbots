package microbots.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.BiConsumer;
import microbots.Action;
import microbots.MicrobotProcessingUnit;
import microbots.Surroundings;

/**
 * Entry point for configuring a microbot battle simulation. Example usage:
 *
 * <pre>
 *   static final class Microbot9000 extends MicrobotProcesingUnit { ... }
 *   static final class MicrobotPrime extends MicrobotProcesingUnit { ... }
 *   ...
 *   Simulation simulation = Simulation.builder()
 *       .setPopulationSize(500)
 *       .addMpuType(Microbot9000.class)
 *       .addMpuType(MicrobotPrime.class)
 *       .build();
 *   simulation.run();
 * </pre>
 */
public final class Simulation {

  @FunctionalInterface
  private interface ActionDelegate extends BiConsumer<Simulation, Microbot> {}

  private static final ImmutableMap<Action, ActionDelegate> ACTION_DELEGATES =
      ImmutableMap.of(
          Action.WAIT, Simulation::handleWait,
          Action.MOVE, Simulation::handleMove,
          Action.ROTATE_LEFT, Simulation::handleRotateLeft,
          Action.ROTATE_RIGHT, Simulation::handleRotateRight,
          Action.HACK, Simulation::handleHack);

  private final ImmutableList<Microbot> microbots;
  private final Arena arena;
  private final Window window;

  private Simulation(ImmutableList<Microbot> microbots, Arena arena, Window window) {
    this.microbots = microbots;
    this.arena = arena;
    this.window = window;
  }

  /** Runs the simulation! */
  @SuppressWarnings("InfiniteLoopStatement")
  public void run() throws Exception {
    window.setVisible(true);

    while (true) {
      microbots.forEach(this::doTurn);
      window.repaint();
      Thread.sleep(125L);
    }
  }

  /** Performs a single turn for the specified microbot. */
  private void doTurn(Microbot microbot) {
    Surroundings surroundings = arena.getMicrobotSurroundings(microbot);
    Action action = microbot.getAction(surroundings);
    ACTION_DELEGATES.getOrDefault(action, Simulation::handleUnknownAction).accept(this, microbot);
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
    return new Builder();
  }

  /** Builder class for constructing simulation instances. */
  public static final class Builder {

    private int populationSize = 250;
    private final HashSet<Class<? extends MicrobotProcessingUnit>> mpuTypes = new HashSet<>();

    /**
     * Sets the population size, which is the number of microbots of each type that will be included
     * in the simulation. Must be positive.
     */
    public Builder setPopulationSize(int populationSize) {
      checkArgument(populationSize > 0, "populationSize must be positive.");
      this.populationSize = populationSize;
      return this;
    }

    /** Adds a new {@link MicrobotProcessingUnit MPU type} to be included in the simulation. */
    public Builder addMpuType(Class<? extends MicrobotProcessingUnit> mpuType) {
      checkNotNull(mpuType);
      mpuTypes.add(mpuType);
      return this;
    }

    /** Returns a new simulation instance based on the parameters of this builder. */
    public Simulation build() throws Exception {
      checkArgument(
          !mpuTypes.isEmpty(), "Must specify at least one MPU type to create a simulation.");

      // Data model parameters.
      ImmutableList<Microbot> microbots = MicrobotFactory.create(populationSize).ofEach(mpuTypes);
      Arena arena = Arena.builder().withMicrobots(microbots).build();

      // View parameters.
      ArenaView arenaView = ArenaView.of(arena);
      PopulationView populationView = PopulationView.of(arena);
      Window window =
          Window.builder().setArenaView(arenaView).setPopulationView(populationView).build();

      return new Simulation(microbots, arena, window);
    }
  }
}
