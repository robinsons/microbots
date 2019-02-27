package microbots.core;

import com.google.common.eventbus.Subscribe;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JFrame;

/** The window holds UI components that show the simulation as it runs. */
final class Window extends JFrame {

  private static final String WINDOW_TITLE = "Microbot Battle Arena";

  private Component windowPanel;

  private Window() {}

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    Events.WINDOW_REPAINT_DONE.post("bar");
  }

  /** Callback for {@link Events#SIMULATION_ROUND_DONE}. */
  @Subscribe
  public void onSimulationRoundDone(String ignored) {
    repaint();
  }

  /** Callback for {@link Events#SIMULATION_BUILD_NEW_CALLED}. */
  @Subscribe
  public void onSimulationBuildNewCalled(Simulation simulation) {
    setSimulation(simulation);
  }

  /**
   * Sets simulation that this window will display. Removes the existing simulation and its UI
   * components, if any.
   */
  private void setSimulation(Simulation simulation) {
    reset();

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
    windowPanel = null;
  }

  /** Returns a new {@link Window}. */
  static Window create() {
    Window window = new Window();
    window.setTitle(WINDOW_TITLE);
    window.setResizable(false);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setJMenuBar(WindowMenuBar.create());

    Events.SIMULATION_ROUND_DONE.register(window);
    Events.SIMULATION_BUILD_NEW_CALLED.register(window);

    return window;
  }
}
