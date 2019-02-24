package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static microbots.core.GraphicsUtil.drawAndPreserveTransform;
import static microbots.core.UIConstants.BACKGROUND_COLOR;
import static microbots.core.UIConstants.MICROBOT_HALF_INNER_SIZE_DOUBLE_PX;
import static microbots.core.UIConstants.MICROBOT_INNER_SIZE_PX;
import static microbots.core.UIConstants.MICROBOT_NORTH_FACING_VECTOR_SHAPE;
import static microbots.core.UIConstants.MICROBOT_OUTER_SIZE_PX;
import static microbots.core.UIConstants.MICROBOT_PADDING_PX;

import com.google.common.collect.ImmutableTable;
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
    // Draw the terrain first. This way if we have a bug in the drawing code, we should see some
    // microbots being drawn on top of the terrain.
    drawTerrain(arena.terrain(), g2);
    arena.microbots().forEach(microbot -> drawMicrobot(microbot, g2));
  }

  /** Draws a single microbot. */
  private static void drawMicrobot(Microbot microbot, Graphics2D g2) {
    int x = MICROBOT_PADDING_PX + MICROBOT_OUTER_SIZE_PX * microbot.column();
    int y = MICROBOT_PADDING_PX + MICROBOT_OUTER_SIZE_PX * microbot.row();
    drawAndPreserveTransform(
        g2,
        g2d -> {
          // Draw a square as the outer body.
          g2d.translate(x, y);
          g2d.setColor(microbot.color());
          g2d.fillRect(0, 0, MICROBOT_INNER_SIZE_PX, MICROBOT_INNER_SIZE_PX);

          // Draw the vector shape inside the body to indicate facing direction.
          g2d.rotate(
              microbot.facing().compassAngleRadians(),
              MICROBOT_HALF_INNER_SIZE_DOUBLE_PX,
              MICROBOT_HALF_INNER_SIZE_DOUBLE_PX);
          g2d.setColor(BACKGROUND_COLOR);
          g2d.fill(MICROBOT_NORTH_FACING_VECTOR_SHAPE);
        });
  }

  /** Draws the given terrain. */
  private static void drawTerrain(
      ImmutableTable<Integer, Integer, Terrain> terrain, Graphics2D g2) {
    terrain
        .cellSet()
        .forEach(
            cell ->
                cell.getValue()
                    .color()
                    .ifPresent(
                        color -> {
                          int x = MICROBOT_OUTER_SIZE_PX * cell.getColumnKey();
                          int y = MICROBOT_OUTER_SIZE_PX * cell.getRowKey();
                          drawAndPreserveTransform(
                              g2,
                              g2d -> {
                                g2d.translate(x, y);
                                g2d.setColor(color);
                                g2d.fillRoundRect(
                                    0,
                                    0,
                                    MICROBOT_OUTER_SIZE_PX,
                                    MICROBOT_OUTER_SIZE_PX,
                                    MICROBOT_INNER_SIZE_PX,
                                    MICROBOT_INNER_SIZE_PX);
                              });
                        }));
  }

  /** Returns a new view of the given {@link Arena}. */
  static ArenaView createFor(Arena arena) {
    checkNotNull(arena);
    int width = MICROBOT_OUTER_SIZE_PX * arena.columns();
    int height = MICROBOT_OUTER_SIZE_PX * arena.rows();
    return new ArenaView(arena, width, height);
  }
}
