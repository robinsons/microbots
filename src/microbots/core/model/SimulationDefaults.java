package microbots.core.model;

/** Container for default values used in the simulation. */
public final class SimulationDefaults {

  // Not intended for instantiation.
  private SimulationDefaults() {}

  public static final int POPULATION_SIZE = 500;
  public static final ArenaMap ARENA_MAP = ArenaMap.ENCLOSED;
  public static final SimulationRate SIMULATION_RATE = SimulationRate.NORMAL;
}
