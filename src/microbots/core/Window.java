package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static microbots.core.UIConstants.INFO_CONTAINER_WIDTH_PX;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/** The window holds UI components that show the simulation as it runs. */
final class Window extends JFrame {

  private static final String WINDOW_TITLE = "Microbot Battle Arena";

  /** Returns a new {@link Builder} for creating window instances. */
  static Builder builder() {
    return new Builder();
  }

  /** Builder for creating window instances. */
  static final class Builder {

    private JPanel arenaView;
    private JPanel populationView;
    private JPanel histogramView;

    /** Sets the component that will show the main arena where microbots are battling. */
    Builder setArenaView(JPanel arenaView) {
      this.arenaView = arenaView;
      return this;
    }

    /** Sets the component that will display microbot population levels during a battle. */
    Builder setPopulationView(JPanel populationView) {
      this.populationView = populationView;
      return this;
    }

    /** Sets the component that will display microbot population history during a battle. */
    Builder setHistogramView(JPanel histogramView) {
      this.histogramView = histogramView;
      return this;
    }

    /** Returns a new {@link Window} instance based on the parameters of this builder. */
    Window build() {
      checkNotNull(arenaView);
      checkNotNull(populationView);
      checkNotNull(histogramView);

      JPanel infoContainer = new JPanel();
      infoContainer.setPreferredSize(new Dimension(INFO_CONTAINER_WIDTH_PX, arenaView.getHeight()));
      infoContainer.setLayout(new BorderLayout(0, 0));
      infoContainer.add(populationView, BorderLayout.CENTER);
      infoContainer.add(histogramView, BorderLayout.SOUTH);

      JPanel mainContainer = new JPanel();
      mainContainer.setLayout(new BorderLayout(0, 0));
      mainContainer.add(arenaView, BorderLayout.CENTER);
      mainContainer.add(infoContainer, BorderLayout.EAST);

      Window window = new Window();
      window.add(mainContainer);
      window.pack();

      window.setTitle(WINDOW_TITLE);
      window.setResizable(false);
      window.setLocationRelativeTo(null);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      return window;
    }
  }
}
