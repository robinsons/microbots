package microbots.core;

import java.awt.Color;

/** Holds constants related to the UI aspects of the simulation. */
final class UIConstants {
  private UIConstants() {}

  static final Color BACKGROUND_COLOR = Color.DARK_GRAY;

  // Microbot sizes in the arena.
  static final int MICROBOT_PADDING_PX = 1;
  static final int MICROBOT_INNER_SIZE_PX = 8;
  static final int MICROBOT_OUTER_SIZE_PX = MICROBOT_INNER_SIZE_PX + 2 * MICROBOT_PADDING_PX;

  // The info container side panel.
  static final int INFO_CONTAINER_WIDTH_PX = 250;
}
