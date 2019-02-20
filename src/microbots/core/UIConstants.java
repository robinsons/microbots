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

  static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
  static final String FONT_FILENAME = "ARDESTINE.ttf";
  static final Font BASE_FONT = loadFont();

  // Microbot sizes in the arena.
  static final int MICROBOT_PADDING_PX = 1;
  static final int MICROBOT_INNER_SIZE_PX = 10;
  static final int MICROBOT_OUTER_SIZE_PX = MICROBOT_INNER_SIZE_PX + 2 * MICROBOT_PADDING_PX;
  static final int MICROBOT_HALF_INNER_SIZE_INT_PX = MICROBOT_INNER_SIZE_PX / 2;
  static final double MICROBOT_HALF_INNER_SIZE_DOUBLE_PX = MICROBOT_INNER_SIZE_PX / 2.0d;
  static final Shape MICROBOT_NORTH_FACING_VECTOR_SHAPE =
      new Polygon(
          new int[] {
            MICROBOT_HALF_INNER_SIZE_INT_PX,
            MICROBOT_INNER_SIZE_PX - MICROBOT_PADDING_PX,
            MICROBOT_PADDING_PX
          },
          new int[] {
            MICROBOT_PADDING_PX, MICROBOT_HALF_INNER_SIZE_INT_PX, MICROBOT_HALF_INNER_SIZE_INT_PX
          },
          3);

  // The info container side panel.
  static final int SIDE_VIEW_WIDTH_PX = 250;

  /** Loads the font specified by {@link #FONT_FILENAME}. */
  private static Font loadFont() {
    Path path = Paths.get(System.getProperty("user.dir"), "res", FONT_FILENAME);
    try (InputStream inputStream = Files.newInputStream(path)) {
      return Font.createFont(Font.TRUETYPE_FONT, inputStream);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
