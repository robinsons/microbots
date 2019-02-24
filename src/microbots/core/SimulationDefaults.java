package microbots.core;

/** Container for default values used in the simulation. */
final class SimulationDefaults {

  // Not intended for instantiation.
  private SimulationDefaults() {}

  static final int POPULATION_SIZE = 300;
  static final ArenaMap ARENA_MAP = ArenaMap.ENCLOSED;
  static final SimulationRate SIMULATION_RATE = SimulationRate.NORMAL;
}
