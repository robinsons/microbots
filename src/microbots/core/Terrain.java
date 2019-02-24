package microbots.core;

import static com.google.common.collect.MoreCollectors.toOptional;

import java.awt.Color;
import java.util.Optional;
import java.util.stream.Stream;

/** Terrain are static, non-microbot features that may be present in the arena. */
enum Terrain {

  /** A field is just an empty space on a map. Microbots can move onto this terrain. */
  FIELD(' ', true, Optional.empty()),

  /** A wall blocks microbot movement. */
  WALL('w', false, Optional.of(UIConstants.WALL_COLOR));

  private final char symbol;
  private final boolean isTraversable;
  private final Optional<Color> color;

  Terrain(char symbol, boolean isTraversable, Optional<Color> color) {
    this.symbol = symbol;
    this.isTraversable = isTraversable;
    this.color = color;
  }

  /** Returns whether or not this terrain allows microbots to move onto it. */
  boolean isTraversable() {
    return isTraversable;
  }

  /**
   * Returns the color that should be used when drawing this terrain. If {@link Optional#empty()} is
   * returned, then this terrain should not be drawn.
   */
  Optional<Color> color() {
    return color;
  }

  /**
   * Returns the unique terrain with the given symbol, or else {@link Optional#empty()} if there is
   * none.
   */
  static Optional<Terrain> forSymbol(char symbol) {
    return Stream.of(values()).filter(terrain -> terrain.symbol == symbol).collect(toOptional());
  }
}
