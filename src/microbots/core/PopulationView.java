package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static microbots.core.UIConstants.BACKGROUND_COLOR;
import static microbots.core.UIConstants.INFO_CONTAINER_WIDTH_PX;
import static microbots.core.UIConstants.MICROBOT_OUTER_SIZE_PX;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import microbots.core.PopulationSnapshot.Population;

/** Shows the remaining population of each microbot type in the battle. */
final class PopulationView extends JPanel {

  private static final String FONT_FILENAME = "ARDESTINE.ttf";
  private static final float FONT_SIZE = 24f;
  private static final int INSET_PX = 10;
  private static final long UPDATE_FREQUENCY_MILLIS = 500L;

  private static final Comparator<Population> DESCENDING_BY_POPULATION_SIZE =
      Comparator.comparingInt(Population::size).reversed();

  private PopulationSnapshot snapshot;

  private PopulationView(PopulationSnapshot snapshot) {
    this.snapshot = snapshot;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    snapshot = snapshot.refreshIfOlderThan(UPDATE_FREQUENCY_MILLIS);
    ImmutableList<Population> populations =
        ImmutableList.sortedCopyOf(DESCENDING_BY_POPULATION_SIZE, snapshot.populations());

    int position = 0;
    for (Population population : populations) {
      position += FONT_SIZE;
      showMicrobotPopulation(population, position, g2);
    }
  }

  /** Shows the indicated microbot's population. */
  private void showMicrobotPopulation(Population population, int position, Graphics g) {
    drawStringWithShadow(population.name(), population.color(), INSET_PX, position, g);

    String populationText = String.format("%d", population.size());
    int populationWidth = g.getFontMetrics().stringWidth(populationText);
    drawStringWithShadow(
        populationText, population.color(), getWidth() - populationWidth - INSET_PX, position, g);
  }

  /** Draws the given text at the position with the provided color and a shadow behind it. */
  private static void drawStringWithShadow(String text, Color textColor, int x, int y, Graphics g) {
    g.setColor(Color.BLACK);
    g.drawString(text, x - 1, y - 1);
    g.setColor(textColor);
    g.drawString(text, x, y);
  }

  /** Returns a new view for the given {@link Arena}. */
  static PopulationView of(Arena arena) throws Exception {
    checkNotNull(arena);

    int width = INFO_CONTAINER_WIDTH_PX;
    int height = 3 * MICROBOT_OUTER_SIZE_PX * arena.rows() / 4;

    PopulationView populationView = new PopulationView(PopulationSnapshot.of(arena));
    populationView.setPreferredSize(new Dimension(width, height));
    populationView.setBackground(BACKGROUND_COLOR);
    populationView.setFont(loadFont());
    populationView.setBorder(BorderFactory.createRaisedBevelBorder());

    return populationView;
  }

  /** Loads the font specified by {@link #FONT_FILENAME}. */
  private static Font loadFont() throws Exception {
    Path path = Paths.get(System.getProperty("user.dir"), "res", FONT_FILENAME);
    try (InputStream inputStream = Files.newInputStream(path)) {
      return Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(FONT_SIZE);
    }
  }
}
