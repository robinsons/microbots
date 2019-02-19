package microbots.core;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A {@link View} is a lightweight analog to a {@link javax.swing.JPanel}. Rather than adding
 * several JPanels to a {@link javax.swing.JFrame} and mucking with layouts, multiple Views are
 * intended to be drawn to a single JPanel.
 */
abstract class View {

  private final int width;
  private final int height;
  private final Color backgroundColor;

  View(int width, int height, Color backgroundColor) {
    this.width = width;
    this.height = height;
    this.backgroundColor = backgroundColor;
  }

  final int width() {
    return width;
  }

  final int height() {
    return height;
  }

  /** Draws this {@link View} on top of a rectangular background. */
  final void paintWithBackground(Graphics2D g2) {
    g2.setColor(backgroundColor);
    g2.fillRect(0, 0, width, height);
    paint(g2);
  }

  /** Draws this {@link View}. */
  abstract void paint(Graphics2D g2);
}
