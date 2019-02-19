package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static microbots.core.GraphicsUtil.drawStringWithShadow;
import static microbots.core.UIConstants.BACKGROUND_COLOR;
import static microbots.core.UIConstants.BASE_FONT;
import static microbots.core.UIConstants.MICROBOT_OUTER_SIZE_PX;
import static microbots.core.UIConstants.SIDE_VIEW_WIDTH_PX;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Comparator;
import microbots.core.PopulationSnapshot.Population;

/** Shows the remaining population of each microbot type in the battle. */
final class PopulationView extends View {

  private static final float FONT_SIZE = 24f;
  private static final Color SHADOW_COLOR = Color.BLACK;
  private static final int SHADOW_OFFSET = -1;
  private static final int TEXT_INSET_PX = 10;
  private static final long UPDATE_FREQUENCY_MILLIS = 500L;

  private static final Comparator<Population> DESCENDING_BY_POPULATION_SIZE =
      Comparator.comparingInt(Population::size).reversed();

  private PopulationSnapshot snapshot;

  private final Font font;

  private PopulationView(PopulationSnapshot snapshot, Font font, int width, int height) {
    super(width, height, BACKGROUND_COLOR);
    this.snapshot = snapshot;
    this.font = font;
  }

  @Override
  public void paint(Graphics2D g2) {
    g2.setFont(font);

    snapshot = snapshot.refreshIfOlderThan(UPDATE_FREQUENCY_MILLIS);
    ImmutableList<Population> populations =
        ImmutableList.sortedCopyOf(DESCENDING_BY_POPULATION_SIZE, snapshot.populations());

    int yPosition = 0;
    for (Population population : populations) {
      yPosition += FONT_SIZE;
      drawMicrobotPopulation(population, yPosition, g2);
    }
  }

  /** Draws the indicated microbot's population. */
  private void drawMicrobotPopulation(Population population, int yPosition, Graphics g) {
    drawStringWithShadow(
        g,
        population.name(),
        TEXT_INSET_PX,
        yPosition,
        population.color(),
        SHADOW_COLOR,
        SHADOW_OFFSET);

    String populationText = String.format("%d", population.size());
    int populationTextWidth = g.getFontMetrics().stringWidth(populationText);
    drawStringWithShadow(
        g,
        populationText,
        width() - populationTextWidth - TEXT_INSET_PX,
        yPosition,
        population.color(),
        SHADOW_COLOR,
        SHADOW_OFFSET);
  }

  /** Returns a new view for the given {@link Arena}. */
  static PopulationView createFor(Arena arena) {
    checkNotNull(arena);
    int width = SIDE_VIEW_WIDTH_PX;
    int height = 3 * MICROBOT_OUTER_SIZE_PX * arena.rows() / 4;
    return new PopulationView(
        PopulationSnapshot.of(arena), BASE_FONT.deriveFont(FONT_SIZE), width, height);
  }
}
