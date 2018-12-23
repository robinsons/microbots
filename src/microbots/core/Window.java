package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.swing.*;

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

    /** Returns a new {@link Window} instance based on the parameters of this builder. */
    Window build() {
      checkNotNull(arenaView);
      checkNotNull(populationView);

      JPanel container = new JPanel();
      container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
      container.add(arenaView);
      container.add(populationView);

      Window window = new Window();
      window.add(container);
      window.pack();

      window.setTitle(WINDOW_TITLE);
      window.setResizable(false);
      window.setLocationRelativeTo(null);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      return window;
    }
  }
}
