package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static microbots.core.ArenaView.MICROBOT_BOUNDARY_SIZE_PX;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSetMultimap;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** Shows the remaining population of each microbot type in the battle. */
final class PopulationView extends JPanel {

  private static final String FONT_FILENAME = "ARDESTINE.ttf";
  private static final float FONT_SIZE = 24f;
  private static final int INSET_PX = 10;
  private static final long UPDATE_FREQUENCY_MILLIS = 500L;

  private long lastUpdateTimeMillis;
  private ImmutableList<MicrobotSnapshot> snapshots;

  private final Arena arena;

  private PopulationView(Arena arena) {
    this.arena = arena;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (System.currentTimeMillis() - lastUpdateTimeMillis >= UPDATE_FREQUENCY_MILLIS || snapshots == null) {
      lastUpdateTimeMillis = System.currentTimeMillis();
      snapshots =
          arena
              .microbots()
              .stream()
              .collect(
                  ImmutableSetMultimap.toImmutableSetMultimap(Microbot::name, Function.identity()))
              .asMap()
              .entrySet()
              .stream()
              .map(
                  entry ->
                      new MicrobotSnapshot(
                          entry.getKey(),
                          entry.getValue().size(),
                          entry
                              .getValue()
                              .stream()
                              .findAny()
                              .map(Microbot::color)
                              .orElse(Color.WHITE)))
              .sorted(
                  Comparator.<MicrobotSnapshot>comparingInt(snapshot -> snapshot.populationSize)
                      .reversed())
              .collect(ImmutableList.toImmutableList());
    }

    int position = 0;
    for (MicrobotSnapshot snapshot : snapshots) {
      position += FONT_SIZE;
      showMicrobotPopulation(snapshot, position, g);
    }
  }

  /** Shows the indicated microbot's population. */
  private void showMicrobotPopulation(MicrobotSnapshot snapshot, int position, Graphics g) {
    g.setColor(snapshot.color);
    g.drawString(snapshot.name, INSET_PX, position);

    String population = String.format("%d", snapshot.populationSize);
    int populationWidth = g.getFontMetrics().stringWidth(population);
    g.drawString(population, getWidth() - populationWidth - INSET_PX, position);
  }

  /** Returns a new view for the given {@link Arena}. */
  static PopulationView of(Arena arena) throws Exception {
    checkNotNull(arena);

    int width = 250;
    int height = MICROBOT_BOUNDARY_SIZE_PX * arena.rows();

    PopulationView populationView = new PopulationView(arena);
    populationView.setPreferredSize(new Dimension(width, height));
    populationView.setBackground(Color.DARK_GRAY);
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

  /** A snapshot of a microbot population at some point in time. */
  private static final class MicrobotSnapshot {
    private final String name;
    private final int populationSize;
    private final Color color;

    private MicrobotSnapshot(String name, int populationSize, Color color) {
      this.name = name;
      this.populationSize = populationSize;
      this.color = color;
    }
  }
}
