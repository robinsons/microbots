package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static microbots.core.UIConstants.BACKGROUND_COLOR;
import static microbots.core.UIConstants.MICROBOT_INNER_SIZE_PX;
import static microbots.core.UIConstants.MICROBOT_OUTER_SIZE_PX;
import static microbots.core.UIConstants.MICROBOT_PADDING_PX;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** Shows the positions of the microbots in the arena. */
final class ArenaView extends JPanel {

  private final Arena arena;

  private ArenaView(Arena arena) {
    this.arena = arena;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    arena.microbots().forEach(microbot -> drawMicrobot(microbot, g2));
  }

  /** Draws a single microbot. */
  private static void drawMicrobot(Microbot microbot, Graphics2D g2) {
    int x = MICROBOT_PADDING_PX + MICROBOT_OUTER_SIZE_PX * microbot.column();
    int y = MICROBOT_PADDING_PX + MICROBOT_OUTER_SIZE_PX * microbot.row();
    g2.setColor(microbot.color());
    g2.fillRect(x, y, MICROBOT_INNER_SIZE_PX, MICROBOT_INNER_SIZE_PX);
  }

  /** Returns a new view of the given {@link Arena}. */
  static ArenaView of(Arena arena) {
    checkNotNull(arena);

    int width = MICROBOT_OUTER_SIZE_PX * arena.columns();
    int height = MICROBOT_OUTER_SIZE_PX * arena.rows();

    ArenaView arenaView = new ArenaView(arena);
    arenaView.setPreferredSize(new Dimension(width, height));
    arenaView.setBackground(BACKGROUND_COLOR);
    arenaView.setBorder(BorderFactory.createRaisedBevelBorder());

    return arenaView;
  }
}
