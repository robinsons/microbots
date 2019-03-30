package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static microbots.core.GraphicsUtil.drawAndPreserveTransform;
import static microbots.core.UIConstants.ARENA_CELL_SIZE_PX;
import static microbots.core.UIConstants.BACKGROUND_COLOR;
import static microbots.core.UIConstants.MICROBOT_DIRECTIONAL_VECTOR_COLOR;
import static microbots.core.UIConstants.MICROBOT_HALF_SIZE_DOUBLE_PX;
import static microbots.core.UIConstants.MICROBOT_NORTH_FACING_VECTOR_SHAPE;
import static microbots.core.UIConstants.MICROBOT_PADDING_PX;
import static microbots.core.UIConstants.MICROBOT_SIZE_PX;
import static microbots.core.UIConstants.WALL_COLOR;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/** Shows the positions of the microbots in the arena. */
final class ArenaView extends View {

  private static final ImmutableMap<Terrain, Supplier<BufferedImage>> TERRAIN_IMAGE_SUPPLIERS =
      ImmutableMap.of(Terrain.WALL, ArenaView::createWallImage);
  private static final ImmutableMap<Terrain, BufferedImage> TERRAIN_IMAGES = createTerrainImages();

  private static HashMap<Class<?>, BufferedImage> MICROBOT_IMAGES = new HashMap<>();

  private final Arena arena;

  private ArenaView(Arena arena, int width, int height) {
    super(width, height, BACKGROUND_COLOR);
    this.arena = arena;
  }

  @Override
  public void paint(Graphics2D g2) {
    drawTerrain(g2, arena.terrain());
    drawMicrobots(g2, arena.microbots());
  }

  /** Draws all of the terrain in the arena. */
  private static void drawTerrain(
      Graphics2D g2, ImmutableTable<Integer, Integer, Terrain> terrain) {
    terrain
        .cellSet()
        .forEach(
            cell ->
                drawArenaElement(
                    g2,
                    cell.getRowKey(),
                    cell.getColumnKey(),
                    () -> {
                      if (TERRAIN_IMAGES.containsKey(cell.getValue())) {
                        g2.drawImage(TERRAIN_IMAGES.get(cell.getValue()), null, 0, 0);
                      }
                    }));
  }

  /** Draws all of the microbots in the arena. */
  private static void drawMicrobots(Graphics2D g2, ImmutableSet<Microbot> microbots) {
    microbots.forEach(
        microbot -> {
          maybeCreateMicrobotImage(microbot);
          drawArenaElement(
              g2,
              microbot.row(),
              microbot.column(),
              () -> {
                g2.rotate(
                    microbot.facing().compassAngleRadians(),
                    MICROBOT_PADDING_PX + MICROBOT_HALF_SIZE_DOUBLE_PX,
                    MICROBOT_PADDING_PX + MICROBOT_HALF_SIZE_DOUBLE_PX);
                g2.drawImage(MICROBOT_IMAGES.get(microbot.mpuType()), null, 0, 0);
              });
        });
  }

  /**
   * Draws a single element of the arena - e.g. a microbot or terrain feature - at the indicated row
   * and column by invoking the provided {@code drawDelegate} within a call to {@link
   * GraphicsUtil#drawAndPreserveTransform(Graphics2D, Runnable)}. The Graphics2D object will be
   * positioned to the correct location for the delegate to draw its element.
   */
  private static void drawArenaElement(Graphics2D g2, int row, int column, Runnable drawDelegate) {
    int x = ARENA_CELL_SIZE_PX * column;
    int y = ARENA_CELL_SIZE_PX * row;
    drawAndPreserveTransform(
        g2,
        () -> {
          g2.translate(x, y);
          drawDelegate.run();
        });
  }

  /**
   * Checks if {@link #MICROBOT_IMAGES} contains an entry for the microbot type with a given name,
   * and creates one if not. Caching microbot images and drawing those images is more efficient than
   * direct calls to methods like {@link Graphics2D#fill(Shape)}.
   */
  private static void maybeCreateMicrobotImage(Microbot microbot) {
    if (!MICROBOT_IMAGES.containsKey(microbot.mpuType())) {
      BufferedImage image =
          createArenaElementImage(
              g2 -> {
                g2.translate(MICROBOT_PADDING_PX, MICROBOT_PADDING_PX);
                g2.setColor(microbot.color());
                g2.fillRect(0, 0, MICROBOT_SIZE_PX, MICROBOT_SIZE_PX);
                g2.setColor(MICROBOT_DIRECTIONAL_VECTOR_COLOR);
                g2.fill(MICROBOT_NORTH_FACING_VECTOR_SHAPE);
              });
      MICROBOT_IMAGES.put(microbot.mpuType(), image);
    }
  }

  /**
   * Creates an image for each type of {@link Terrain}. Using static images nets a performance gain
   * over using methods like {@link Graphics2D#fill(Shape)};
   */
  private static ImmutableMap<Terrain, BufferedImage> createTerrainImages() {
    return Stream.of(Terrain.values())
        .filter(TERRAIN_IMAGE_SUPPLIERS::containsKey)
        .collect(
            toImmutableMap(
                Function.identity(), terrain -> TERRAIN_IMAGE_SUPPLIERS.get(terrain).get()));
  }

  /** Delegate that creates the image for {@link Terrain#WALL}. */
  private static BufferedImage createWallImage() {
    return createArenaElementImage(
        g2 -> {
          g2.setColor(WALL_COLOR);
          g2.fillRoundRect(
              0, 0, ARENA_CELL_SIZE_PX, ARENA_CELL_SIZE_PX, MICROBOT_SIZE_PX, MICROBOT_SIZE_PX);
        });
  }

  /**
   * Returns a new image whose buffer is filled by invoking the given {@code imageDrawer}. As arena
   * elements must fit within a square of side length {@link UIConstants#ARENA_CELL_SIZE_PX}, the
   * image will be initialized to this size.
   */
  private static BufferedImage createArenaElementImage(Consumer<Graphics2D> imageDrawer) {
    BufferedImage image =
        new BufferedImage(ARENA_CELL_SIZE_PX, ARENA_CELL_SIZE_PX, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    imageDrawer.accept(g2);
    g2.dispose();
    return image;
  }

  /** Returns a new view of the given {@link Arena}. */
  static ArenaView createFor(Arena arena) {
    checkNotNull(arena);
    int width = ARENA_CELL_SIZE_PX * arena.columns();
    int height = ARENA_CELL_SIZE_PX * arena.rows();
    return new ArenaView(arena, width, height);
  }
}
