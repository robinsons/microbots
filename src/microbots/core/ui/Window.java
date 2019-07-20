package microbots.core.ui;

import com.google.common.eventbus.Subscribe;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Collection;
import javax.swing.JFrame;
import microbots.MicrobotProcessingUnit;
import microbots.core.util.Events;
import microbots.core.model.events.SimulationRoundDoneEvent;
import microbots.core.model.events.SimulationRunCalledEvent;
import microbots.core.ui.events.WindowRepaintDoneEvent;

/** The window holds UI components that show the simulation as it runs. */
public final class Window extends JFrame {

  private static final String WINDOW_TITLE = "Microbot Battle Arena";

  private Component windowPanel;

  private Window() {}

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    Events.post(new WindowRepaintDoneEvent());
  }

  @Subscribe
  public void onSimulationRoundDone(SimulationRoundDoneEvent event) {
    repaint();
  }

  @Subscribe
  public void onSimulationRunCalled(SimulationRunCalledEvent event) {
    Component oldWindowPanel = windowPanel;
    windowPanel = add(WindowPanel.createFor(event.simulation().arena()));
    if (oldWindowPanel != null) {
      remove(oldWindowPanel);
    }
    pack();
    if (!isVisible()) {
      setLocationRelativeTo(null);
      setVisible(true);
    }
  }

  /** Creates a new {@link Window}. */
  public static void create(
      Collection<Class<? extends MicrobotProcessingUnit>> selectedMpuTypes, int populationSize) {
    Window window = new Window();
    window.setTitle(WINDOW_TITLE);
    window.setResizable(false);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setJMenuBar(WindowMenuBar.create(selectedMpuTypes, populationSize));

    Events.register(window);
  }
}
