package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static microbots.core.UIConstants.BACKGROUND_COLOR;
import static microbots.core.UIConstants.MICROBOT_INNER_SIZE_PX;
import static microbots.core.UIConstants.MICROBOT_OUTER_SIZE_PX;
import static microbots.core.UIConstants.MICROBOT_PADDING_PX;

import java.awt.Graphics2D;

/** Shows the positions of the microbots in the arena. */
final class ArenaView extends View {

  private final Arena arena;

  private ArenaView(Arena arena, int width, int height) {
    super(width, height, BACKGROUND_COLOR);
    this.arena = arena;
  }

  @Override
  public void paint(Graphics2D g2) {
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
  static ArenaView createFor(Arena arena) {
    checkNotNull(arena);
    int width = MICROBOT_OUTER_SIZE_PX * arena.columns();
    int height = MICROBOT_OUTER_SIZE_PX * arena.rows();
    return new ArenaView(arena, width, height);
  }
}
