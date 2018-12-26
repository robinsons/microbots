package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static microbots.core.ArenaView.MICROBOT_BOUNDARY_SIZE_PX;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import microbots.core.PopulationSnapshot.Population;

/** Shows a histogram displaying microbot populations over time. */
final class HistogramView extends JPanel {

  private static final long TIMELINE_UPDATE_FREQUENCY_MILLIS = 125L;
  private static final long TIMELINE_RETENTION_PERIOD_MILLIS = 5000L;

  private static final Stroke POPULATION_STROKE =
      new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

  private static final Comparator<Population> ALPHABETICAL_BY_NAME =
      Comparator.comparing(Population::name);

  private long xOffsetMillis;

  private final PopulationTimeline timeline;

  private HistogramView(PopulationTimeline timeline) {
    this.timeline = timeline;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    PopulationSnapshot oldestSnapshot = timeline.oldest();

    long elapsedTimeMillis = System.currentTimeMillis() - oldestSnapshot.creationTimeMillis();
    xOffsetMillis =
        elapsedTimeMillis > TIMELINE_RETENTION_PERIOD_MILLIS
            ? elapsedTimeMillis - TIMELINE_RETENTION_PERIOD_MILLIS
            : 0L;

    ImmutableList<Population> oldestPopulations =
        ImmutableList.sortedCopyOf(ALPHABETICAL_BY_NAME, oldestSnapshot.populations());
    oldestPopulations.forEach(
        population ->
            drawPopulationHistory(
                population,
                oldestSnapshot.globalPopulation(),
                oldestSnapshot.creationTimeMillis(),
                (Graphics2D) g));
  }

  private void drawPopulationHistory(
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

    xPoints.add(computeXCoordinate(snapshot, startTimeMillis, xOffsetMillis));
    yPoints.add(
        computeYCoordinate(populationsByName.get(populationName), globalPopulation, getHeight()));
  }

  private static int computeXCoordinate(
      PopulationSnapshot snapshot, long startTimeMillis, long xOffsetMillis) {
    long distanceFromStartInMillis =
        snapshot.creationTimeMillis() - startTimeMillis - xOffsetMillis;
    return (int) (5 * distanceFromStartInMillis / TIMELINE_UPDATE_FREQUENCY_MILLIS);
  }

  private static int computeYCoordinate(
      Population population, int globalPopulation, int componentHeight) {
    int absoluteY = componentHeight - (componentHeight * population.size() / globalPopulation);
    return clamp(absoluteY, 5, componentHeight - 5);
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
  static HistogramView of(Arena arena) {
    checkNotNull(arena);

    int width = 250;
    int height = (MICROBOT_BOUNDARY_SIZE_PX * arena.rows() / 4) + 1;

    HistogramView histogramView =
        new HistogramView(
            PopulationTimeline.snapshot(arena)
                .every(TIMELINE_UPDATE_FREQUENCY_MILLIS)
                .retainFor(TIMELINE_RETENTION_PERIOD_MILLIS));
    histogramView.setPreferredSize(new Dimension(width, height));
    histogramView.setBackground(Color.DARK_GRAY);
    histogramView.setBorder(BorderFactory.createRaisedBevelBorder());

    return histogramView;
  }
}
