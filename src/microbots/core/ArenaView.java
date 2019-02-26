package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static microbots.core.GraphicsUtil.drawAndPreserveTransform;
import static microbots.core.UIConstants.BACKGROUND_COLOR;
import static microbots.core.UIConstants.MICROBOT_HALF_INNER_SIZE_DOUBLE_PX;
import static microbots.core.UIConstants.MICROBOT_INNER_SIZE_PX;
import static microbots.core.UIConstants.MICROBOT_NORTH_FACING_VECTOR_SHAPE;
import static microbots.core.UIConstants.MICROBOT_OUTER_SIZE_PX;
import static microbots.core.UIConstants.MICROBOT_PADDING_PX;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/** Shows the positions of the microbots in the arena. */
final class ArenaView extends View {

  private static final ImmutableMap<Terrain, BufferedImage> TERRAIN_IMAGES = createTerrainImages();
  private static HashMap<String, BufferedImage> MICROBOT_IMAGES = new HashMap<>();

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
    maybeCreateMicrobotImage(microbot);

    int x = MICROBOT_PADDING_PX + MICROBOT_OUTER_SIZE_PX * microbot.column();
    int y = MICROBOT_PADDING_PX + MICROBOT_OUTER_SIZE_PX * microbot.row();
    BufferedImage image = MICROBOT_IMAGES.get(microbot.name());

    drawAndPreserveTransform(
        g2,
        g2d -> {
          g2d.translate(x, y);
          g2d.rotate(
              microbot.facing().compassAngleRadians(),
              MICROBOT_HALF_INNER_SIZE_DOUBLE_PX,
              MICROBOT_HALF_INNER_SIZE_DOUBLE_PX);
          g2d.drawImage(image, null, 0, 0);
        });
  }

  /** Draws the given terrain. */
  private static void drawTerrain(
      ImmutableTable<Integer, Integer, Terrain> terrain, Graphics2D g2) {
    terrain
        .cellSet()
        .forEach(
            cell -> {
              int x = MICROBOT_OUTER_SIZE_PX * cell.getColumnKey();
              int y = MICROBOT_OUTER_SIZE_PX * cell.getRowKey();
              BufferedImage image = TERRAIN_IMAGES.get(cell.getValue());
              drawAndPreserveTransform(
                  g2,
                  g2d -> {
                    g2d.translate(x, y);
                    g2d.drawImage(image, null, 0, 0);
                  });
            });
  }

  /**
   * Checks if {@link #MICROBOT_IMAGES} contains an entry for the microbot type with a given name,
   * and creates one if not. Caching microbot images and drawing those images is more efficient than
   * direct calls to methods like {@link Graphics2D#fill(Shape)}.
   */
  private static void maybeCreateMicrobotImage(Microbot microbot) {
    if (!MICROBOT_IMAGES.containsKey(microbot.name())) {
      BufferedImage image =
          new BufferedImage(
              MICROBOT_INNER_SIZE_PX, MICROBOT_INNER_SIZE_PX, BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = image.createGraphics();

      graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      graphics.setColor(microbot.color());
      graphics.fillRect(0, 0, MICROBOT_INNER_SIZE_PX, MICROBOT_INNER_SIZE_PX);
      graphics.setColor(BACKGROUND_COLOR);
      graphics.fill(MICROBOT_NORTH_FACING_VECTOR_SHAPE);
      graphics.dispose();

      MICROBOT_IMAGES.put(microbot.name(), image);
    }
  }

  /**
   * Creates an image for each type of {@link Terrain}. Using static images nets a performance gain
   * over using methods like {@link Graphics2D#fill(Shape)};
   */
  private static ImmutableMap<Terrain, BufferedImage> createTerrainImages() {
    return Stream.of(Terrain.values())
        .collect(
            toImmutableMap(
                Function.identity(),
                terrain -> {
                  BufferedImage image =
                      new BufferedImage(
                          MICROBOT_OUTER_SIZE_PX,
                          MICROBOT_OUTER_SIZE_PX,
                          BufferedImage.TYPE_INT_ARGB);
                  Graphics2D graphics = image.createGraphics();

                  graphics.setRenderingHint(
                      RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  graphics.setColor(terrain.color().orElse(BACKGROUND_COLOR));
                  graphics.fillRoundRect(
                      0,
                      0,
                      MICROBOT_OUTER_SIZE_PX,
                      MICROBOT_OUTER_SIZE_PX,
                      MICROBOT_INNER_SIZE_PX,
                      MICROBOT_INNER_SIZE_PX);
                  graphics.dispose();

                  return image;
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
