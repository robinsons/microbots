package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static microbots.core.ArenaView.MICROBOT_BOUNDARY_SIZE_PX;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Comparator;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import microbots.core.PopulationSnapshot.Population;

/** Shows a histogram displaying microbot populations over time. */
final class HistogramView extends JPanel {

  private static final Comparator<Population> ALPHABETICAL_BY_NAME =
      Comparator.comparing(Population::name);

  private final Arena arena;

  private HistogramView(Arena arena) {
    this.arena = arena;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
  }

  /** Returns a new view for the given {@link Arena}. */
  static HistogramView of(Arena arena) {
    checkNotNull(arena);

    int width = 250;
    int height = (MICROBOT_BOUNDARY_SIZE_PX * arena.rows() / 4) + 1;

    HistogramView histogramView = new HistogramView(arena);
    histogramView.setPreferredSize(new Dimension(width, height));
    histogramView.setBackground(Color.DARK_GRAY);
    histogramView.setBorder(BorderFactory.createRaisedBevelBorder());

    return histogramView;
  }
}
