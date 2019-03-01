package microbots.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.Shape;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Holds constants related to the UI aspects of the simulation. */
final class UIConstants {

  // Not intended for instantiation.
  private UIConstants() {}

  // Fonts used in the UI.
  static final Font EXO_EXTRA_BOLD = loadFont("Exo-ExtraBold.ttf");
  static final Font RATIONAL_INTEGER = loadFont("RationalInteger.ttf");

  // Colors for various aspects of the UI.
  static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
  static final Color WALL_COLOR = Color.GRAY;
  static final Color MICROBOT_DIRECTIONAL_VECTOR_COLOR = BACKGROUND_COLOR;

  // Sizes of elements of the UI.
  static final int ARENA_CELL_SIZE_PX = 12;
  static final int SIDE_VIEW_WIDTH_PX = 250;

  // Microbot sizes.
  static final int MICROBOT_PADDING_PX = 1;
  static final int MICROBOT_SIZE_PX = ARENA_CELL_SIZE_PX - 2 * MICROBOT_PADDING_PX;
  static final int MICROBOT_HALF_SIZE_INT_PX = MICROBOT_SIZE_PX / 2;
  static final double MICROBOT_HALF_SIZE_DOUBLE_PX = MICROBOT_SIZE_PX / 2.0d;
  static final Shape MICROBOT_NORTH_FACING_VECTOR_SHAPE =
      new Polygon(
          new int[] {
            MICROBOT_HALF_SIZE_INT_PX, MICROBOT_SIZE_PX - MICROBOT_PADDING_PX, MICROBOT_PADDING_PX
          },
          new int[] {MICROBOT_PADDING_PX, MICROBOT_HALF_SIZE_INT_PX, MICROBOT_HALF_SIZE_INT_PX},
          3);

  /** Loads the font specified by {@code filename}. */
  private static Font loadFont(String filename) {
    Path path = Paths.get(System.getProperty("user.dir"), "res", "fonts", filename);
    try (InputStream inputStream = Files.newInputStream(path)) {
      return Font.createFont(Font.TRUETYPE_FONT, inputStream);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load font.", e);
    }
  }
}
