package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static microbots.core.ArenaView.MICROBOT_BOUNDARY_SIZE_PX;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** Shows a histogram displaying microbot populations over time. */
final class HistogramView extends JPanel {

  private static final long TIMELINE_UPDATE_FREQUENCY_MILLIS = 250L;
  private static final long TIMELINE_RETENTION_PERIOD_MILLIS = 5000L;

  private final PopulationTimeline timeline;

  private HistogramView(PopulationTimeline timeline) {
    this.timeline = timeline;
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

    HistogramView histogramView =
        new HistogramView(
            PopulationTimeline.snapshot(arena)
                .every(TIMELINE_UPDATE_FREQUENCY_MILLIS)
                .retainForever());
    histogramView.setPreferredSize(new Dimension(width, height));
    histogramView.setBackground(Color.DARK_GRAY);
    histogramView.setBorder(BorderFactory.createRaisedBevelBorder());

    return histogramView;
  }
}
