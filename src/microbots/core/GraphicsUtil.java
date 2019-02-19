package microbots.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.function.Consumer;

/**
 * Static utility class with helper functions for working with {@link java.awt.Graphics} and {@link
 * java.awt.Graphics2D} instances.
 */
final class GraphicsUtil {

  // Not intended for instantiation.
  private GraphicsUtil() {}

  /** Invokes the provided {@code drawDelegate} and resets the graphics transform when done. */
  static void drawAndPreserveTransform(Graphics2D g2, Consumer<Graphics2D> drawDelegate) {
    AffineTransform transform = g2.getTransform();
    drawDelegate.accept(g2);
    g2.setTransform(transform);
  }

  /**
   * Invokes the provided {@code drawDelegate} with the indicated clipping region. Resets the
   * clipping region when done.
   */
  static void drawWithinBounds(
      Graphics2D g2, int x, int y, int width, int height, Consumer<Graphics2D> drawDelegate) {
    Shape clip = g2.getClip();
    g2.setClip(x, y, width, height);
    drawDelegate.accept(g2);
    g2.setClip(clip);
  }

  /** Draws the specified {@code text} at the desired location with a shadow effect. */
  static void drawStringWithShadow(
      Graphics g, String text, int x, int y, Color textColor, Color shadowColor, int shadowOffset) {
    g.setColor(shadowColor);
    g.drawString(text, x + shadowOffset, y + shadowOffset);
    g.setColor(textColor);
    g.drawString(text, x, y);
  }
}
