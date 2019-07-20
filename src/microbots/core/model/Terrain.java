package microbots.core.model;

import static com.google.common.collect.MoreCollectors.toOptional;

import java.util.Optional;
import java.util.stream.Stream;

/** Terrain are static, non-microbot features that may be present in the arena. */
public enum Terrain {

  /** A field is just an empty space on a map. Microbots can move onto this terrain. */
  FIELD(' ', true),

  /** A wall blocks microbot movement. */
  WALL('w', false);

  private final char symbol;
  private final boolean isTraversable;

  Terrain(char symbol, boolean isTraversable) {
    this.symbol = symbol;
    this.isTraversable = isTraversable;
  }

  /** Returns whether or not this terrain allows microbots to move onto it. */
  boolean isTraversable() {
    return isTraversable;
  }

  /**
   * Returns the unique terrain with the given symbol, or else {@link Optional#empty()} if there is
   * none.
   */
  static Optional<Terrain> forSymbol(char symbol) {
    return Stream.of(values()).filter(terrain -> terrain.symbol == symbol).collect(toOptional());
  }
}
