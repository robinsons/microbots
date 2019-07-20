package microbots.core.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.function.Consumer;

/**
 * Static utility class with helper functions for working with {@link Graphics} and {@link
 * Graphics2D} instances.
 */
public final class GraphicsUtil {

  // Not intended for instantiation.
  private GraphicsUtil() {}

  /**
   * Invokes the provided {@code drawDelegate} and resets the graphics transform when done. This
   * variation of the {@code drawAndPreserveTransform} function is useful if callers would like to
   * provide a method reference.
   */
  public static void drawAndPreserveTransform(Graphics2D g2, Consumer<Graphics2D> drawDelegate) {
    drawAndPreserveTransform(g2, () -> drawDelegate.accept(g2));
  }

  /**
   * Invokes the provided {@code drawDelegate} and resets the graphics transform when done. This
   * variation of the {@code drawAndPreserveTransform} function is useful when callers plan to use a
   * lambda, obviating the need to create a second Graphics2D handle.
   */
  public static void drawAndPreserveTransform(Graphics2D g2, Runnable drawDelegate) {
    AffineTransform transform = g2.getTransform();
    drawDelegate.run();
    g2.setTransform(transform);
  }

  /**
   * Invokes the provided {@code drawDelegate} with the indicated clipping region. Resets the
   * clipping region when done.
   */
  public static void drawWithinBounds(
      Graphics2D g2, int x, int y, int width, int height, Consumer<Graphics2D> drawDelegate) {
    Shape clip = g2.getClip();
    g2.setClip(x, y, width, height);
    drawDelegate.accept(g2);
    g2.setClip(clip);
  }

  /** Draws the specified {@code text} at the desired location with a shadow effect. */
  public static void drawStringWithShadow(
      Graphics g, String text, int x, int y, Color textColor, Color shadowColor, int shadowOffset) {
    g.setColor(shadowColor);
    g.drawString(text, x + shadowOffset, y + shadowOffset);
    g.setColor(textColor);
    g.drawString(text, x, y);
  }
}
