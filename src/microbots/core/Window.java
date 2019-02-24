package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Thread.sleep;

import java.awt.Component;
import javax.swing.JFrame;

/** The window holds UI components that show the simulation as it runs. */
final class Window extends JFrame {

  private static final String WINDOW_TITLE = "Microbot Battle Arena";
  private static final long ROUND_DELAY_MILLIS = 100L;

  private Simulation simulation;
  private Component windowPanel;

  /** Begins a loop to run the simulation and redraw this window. */
  @SuppressWarnings("InfiniteLoopStatement")
  void run() throws Exception {
    while (true) {
      if (simulation != null) {
        simulation.doRound();
        repaint();
        sleep(ROUND_DELAY_MILLIS);
      }
    }
  }

  /**
   * Sets simulation that this window will display. Removes the existing simulation and its UI
   * components, if any.
   */
  void setSimulation(Simulation simulation) {
    reset();

    this.simulation = checkNotNull(simulation);
    this.windowPanel = add(WindowPanel.createFor(simulation.arena()));

    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }

  /**
   * Resets this window by removing components, nulling out the simulation, and making it not
   * visible.
   */
  private void reset() {
    setVisible(false);

    if (windowPanel != null) {
      remove(windowPanel);
    }

    simulation = null;
    windowPanel = null;
  }

  /** Returns a new {@link Window}. */
  static Window create() {
    Window window = new Window();
    window.setTitle(WINDOW_TITLE);
    window.setResizable(false);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    return window;
  }
}
