package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;

/** Offers a view of the {@link Arena} by showing the microbots. */
final class ArenaView extends JPanel {

  private static final int MICROBOT_SIZE_PX = 8;
  private static final int MICROBOT_PADDING_PX = 1;
  private static final int MICROBOT_BOUNDARY_SIZE_PX = MICROBOT_SIZE_PX + 2 * MICROBOT_PADDING_PX;

  /** Returns a new view of the given {@link Arena}. */
  static ArenaView of(Arena arena) {
    checkNotNull(arena);

    int width = MICROBOT_BOUNDARY_SIZE_PX * arena.columns();
    int height = MICROBOT_BOUNDARY_SIZE_PX * arena.rows();

    ArenaView arenaView = new ArenaView();
    arenaView.setPreferredSize(new Dimension(width, height));
    arenaView.setBackground(Color.DARK_GRAY);

    return arenaView;
  }
}
