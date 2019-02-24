package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static microbots.core.GraphicsUtil.drawAndPreserveTransform;
import static microbots.core.GraphicsUtil.drawWithinBounds;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * This is the root (and sole) content panel for the {@link Window}. Since Swing's layouts are a bit
 * clumsy to work with, we opt instead for a single panel that handles rendering each subview.
 */
final class WindowPanel extends JPanel {

  private static final int BORDER_PADDING_PX = 1;
  private static final Color BACKGROUND_COLOR = Color.BLACK;

  private final View arenaView;
  private final View populationView;
  private final View histogramView;

  private WindowPanel(View arenaView, View populationView, View histogramView) {
    this.arenaView = arenaView;
    this.populationView = populationView;
    this.histogramView = histogramView;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    drawAndPreserveTransform(g2, this::paintViews);
  }

  /**
   * Draws each of the subviews to this panel. They are laid out roughly as follows (not to scale):
   *
   * <pre>
   *   +-------------------+----------------+
   *   |     arenaView     | populationView |
   *   |                   |                |
   *   |                   |                |
   *   |                   +----------------+
   *   |                   | histogramView  |
   *   +-------------------+----------------+
   * </pre>
   */
  private void paintViews(Graphics2D g2) {
    g2.translate(BORDER_PADDING_PX, BORDER_PADDING_PX);
    paintView(g2, arenaView);

    g2.translate(arenaView.width() + BORDER_PADDING_PX, 0);
    paintView(g2, populationView);

    g2.translate(0, populationView.height() + BORDER_PADDING_PX);
    paintView(g2, histogramView);
  }

  /** Paints an individual subview. */
  private static void paintView(Graphics2D g2, View view) {
    drawWithinBounds(g2, 0, 0, view.width(), view.height(), view::paintWithBackground);
  }

  /** Returns a new {@link WindowPanel}, with subviews, for the designated {@link Arena}. */
  static WindowPanel createFor(Arena arena) {
    checkNotNull(arena);

    View arenaView = ArenaView.createFor(arena);
    View populationView = PopulationView.createFor(arena);
    View histogramView = HistogramView.createFor(arena);

    WindowPanel windowPanel = new WindowPanel(arenaView, populationView, histogramView);
    windowPanel.setBackground(BACKGROUND_COLOR);
    windowPanel.setPreferredSize(
        new Dimension(
            arenaView.width()
                + Math.max(populationView.width(), histogramView.width())
                + 3 * BORDER_PADDING_PX,
            Math.max(
                arenaView.height() + 2 * BORDER_PADDING_PX,
                populationView.height() + histogramView.height() + 3 * BORDER_PADDING_PX)));

    return windowPanel;
  }
}
