package microbots.core.ui;

import static com.google.common.base.Preconditions.checkNotNull;
import static microbots.core.ui.UIConstants.ARENA_CELL_SIZE_PX;
import static microbots.core.ui.UIConstants.BACKGROUND_COLOR;
import static microbots.core.ui.UIConstants.RATIONAL_INTEGER;
import static microbots.core.ui.UIConstants.SIDE_VIEW_WIDTH_PX;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import microbots.core.Arena;
import microbots.core.ui.PopulationSnapshot.Population;

/** Shows a histogram displaying microbot populations over time. */
final class HistogramView extends View {

  private static final int GRID_POPULATION_INCREMENT = 50;
  private static final int GRID_POPULATION_BUCKET_SIZE = 500;
  private static final Color GRID_COLOR = Color.GRAY;
  private static final float FONT_SIZE = 13f;

  private static final int Y_THRESHOLD_PX = 3;
  private static final long TIMELINE_RETENTION_PERIOD_MILLIS = 5000L;
  private static final double TIMELINE_FILL_RATIO = 0.8;

  private static final Stroke POPULATION_STROKE =
      new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

  private static final Comparator<Population> ALPHABETICAL_BY_NAME =
      Comparator.comparing(Population::name);

  private final PopulationTimeline timeline;
  private final Font font;

  private HistogramView(PopulationTimeline timeline, Font font, int width, int height) {
    super(width, height, BACKGROUND_COLOR);
    this.timeline = timeline;
    this.font = font;
  }

  @Override
  public void paint(Graphics2D g2) {
    PopulationSnapshot oldestSnapshot = timeline.oldest();
    drawGridLines(g2, oldestSnapshot.globalPopulation());

    long elapsedTimeMillis = System.currentTimeMillis() - oldestSnapshot.creationTimeMillis();
    long xAbsolute = width() * elapsedTimeMillis / TIMELINE_RETENTION_PERIOD_MILLIS;
    long xOffset = Math.min(0L, (long) (TIMELINE_FILL_RATIO * width() - xAbsolute));

    // All points drawn hereafter will be shifted by xOffset pixels. This is how we achieve the
    // appearance of the timeline moving to the left after reaching a certain point.
    g2.translate(xOffset, 0);

    // By using the oldest populations we ensure that we include microbot types that have been
    // completely eliminated in the newer snapshots. These populations will slowly fall off the
    // histogram when enough time has passed.
    ImmutableList<Population> oldestPopulations =
        // Sorting alphabetically ensures a stable draw order with respect to the z-axis.
        ImmutableList.sortedCopyOf(ALPHABETICAL_BY_NAME, oldestSnapshot.populations());
    oldestPopulations.forEach(
        population ->
            drawPopulationTimeline(
                population,
                oldestSnapshot.globalPopulation(),
                oldestSnapshot.creationTimeMillis(),
                g2));
  }

  /** Draws grid lines to indicate population thresholds. */
  private void drawGridLines(Graphics2D g2, int globalPopulation) {
    g2.setFont(font);
    g2.setColor(GRID_COLOR);

    int multiplier = 1 + (globalPopulation / GRID_POPULATION_BUCKET_SIZE);
    int increment = multiplier * GRID_POPULATION_INCREMENT;

    for (int populationLevel = 0;
        populationLevel < globalPopulation;
        populationLevel += increment) {
      int y = height() - (height() * populationLevel / globalPopulation);
      String populationText = String.format("%d", populationLevel);
      int textWidth = g2.getFontMetrics().stringWidth(populationText);

      g2.drawString(populationText, width() - textWidth - 2, y - 1);
      g2.drawLine(0, y, width(), y);
    }
  }

  /** Draws a population timeline for a single population. */
  private void drawPopulationTimeline(
      Population population, int globalPopulation, long startTimeMillis, Graphics2D g2) {
    ImmutableList<PopulationSnapshot> snapshots = timeline.snapshots();
    ArrayList<Integer> xPoints = new ArrayList<>(snapshots.size());
    ArrayList<Integer> yPoints = new ArrayList<>(snapshots.size());

    snapshots.forEach(
        snapshot ->
            addSnapshotCoordinates(
                snapshot, population.name(), globalPopulation, startTimeMillis, xPoints, yPoints));

    g2.setColor(population.color());
    g2.setStroke(POPULATION_STROKE);
    g2.drawPolyline(toIntArray(xPoints), toIntArray(yPoints), xPoints.size());
  }

  /**
   * Computes the x and y coordinates to use for the indicated population at the specified snapshot.
   */
  private void addSnapshotCoordinates(
      PopulationSnapshot snapshot,
      String populationName,
      int globalPopulation,
      long startTimeMillis,
      List<Integer> xPoints,
      List<Integer> yPoints) {
    ImmutableMap<String, Population> populationsByName = snapshot.populationsByName();

    if (!populationsByName.containsKey(populationName)) {
      return;
    }

    xPoints.add(computeXCoordinate(snapshot, startTimeMillis));
    yPoints.add(computeYCoordinate(populationsByName.get(populationName), globalPopulation));
  }

  private int computeXCoordinate(PopulationSnapshot snapshot, long startTimeMillis) {
    long distanceFromStartInMillis = snapshot.creationTimeMillis() - startTimeMillis;
    return (int) (width() * distanceFromStartInMillis / TIMELINE_RETENTION_PERIOD_MILLIS);
  }

  private int computeYCoordinate(Population population, int globalPopulation) {
    int absoluteY = height() - (height() * population.size() / globalPopulation);
    return clamp(absoluteY, Y_THRESHOLD_PX, height() - Y_THRESHOLD_PX);
  }

  /** Clamps the specified value within the interval from min to max. */
  private static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }

  /** Converts a list of integers into an int[]. */
  private static int[] toIntArray(List<Integer> integerList) {
    return integerList.stream().mapToInt(i -> i).toArray();
  }

  /** Returns a new view for the given {@link Arena}. */
  static HistogramView createFor(Arena arena) {
    checkNotNull(arena);

    int width = SIDE_VIEW_WIDTH_PX;
    int height = ARENA_CELL_SIZE_PX * arena.rows() / 4;

    return new HistogramView(
        PopulationTimeline.snapshot(arena)
            .onEveryQuery()
            .retainFor(TIMELINE_RETENTION_PERIOD_MILLIS),
        RATIONAL_INTEGER.deriveFont(FONT_SIZE),
        width,
        height);
  }
}
