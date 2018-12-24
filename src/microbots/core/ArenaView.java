package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** Offers a view of the {@link Arena} by showing the microbots. */
final class ArenaView extends JPanel {

  private static final int MICROBOT_SIZE_PX = 8;
  private static final int MICROBOT_PADDING_PX = 1;

  static final int MICROBOT_BOUNDARY_SIZE_PX = MICROBOT_SIZE_PX + 2 * MICROBOT_PADDING_PX;

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
    int x = MICROBOT_PADDING_PX + MICROBOT_BOUNDARY_SIZE_PX * microbot.column();
    int y = MICROBOT_PADDING_PX + MICROBOT_BOUNDARY_SIZE_PX * microbot.row();
    g2.setColor(microbot.color());
    g2.fillRect(x, y, MICROBOT_SIZE_PX, MICROBOT_SIZE_PX);
  }

  /** Returns a new view of the given {@link Arena}. */
  static ArenaView of(Arena arena) {
    checkNotNull(arena);

    int width = MICROBOT_BOUNDARY_SIZE_PX * arena.columns();
    int height = MICROBOT_BOUNDARY_SIZE_PX * arena.rows();

    ArenaView arenaView = new ArenaView(arena);
    arenaView.setPreferredSize(new Dimension(width, height));
    arenaView.setBackground(Color.DARK_GRAY);
    arenaView.setBorder(BorderFactory.createRaisedBevelBorder());

    return arenaView;
  }
}
