package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Thread.sleep;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JFrame;

/** The window holds UI components that show the simulation as it runs. */
final class Window extends JFrame {

  private static final String WINDOW_TITLE = "Microbot Battle Arena";

  private boolean paintComplete;
  private Simulation simulation;
  private Component windowPanel;

  private Window() {}

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    paintComplete = true;
  }

  /** Begins a loop to run the simulation and redraw this window. */
  @SuppressWarnings("InfiniteLoopStatement")
  void run() throws Exception {
    while (true) {
      if (simulation != null) {
        simulation.doRound();

        // The call to repaint() triggers an asynchronous redrawing of the UI. When the simulation
        // rate is very fast, it was observed that sleep() would return and the next round would
        // begin running BEFORE the UI was completely repainted. This caused visual anomalies,
        // such as the population and histogram views drawing microbot info with the incorrect
        // color. The addition of the paintComplete variable is intended to prevent such issues.
        paintComplete = false;
        repaint();
        while (!paintComplete) {
          sleep(menuBar().selectedSimulationRate().millisPerRound());
        }
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
    if (windowPanel != null) {
      remove(windowPanel);
    }

    simulation = null;
    windowPanel = null;
  }

  /** Returns this window's {@link WindowMenuBar}. */
  private WindowMenuBar menuBar() {
    return (WindowMenuBar) getJMenuBar();
  }

  /** Returns a new {@link Window}. */
  static Window create() {
    Window window = new Window();
    window.setTitle(WINDOW_TITLE);
    window.setResizable(false);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setJMenuBar(WindowMenuBar.create(window));
    return window;
  }
}
