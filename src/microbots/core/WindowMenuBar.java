package microbots.core;

import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

/**
 * The menu bar offers options for controlling the running simulation. For example, one can change
 * how quickly the simulation is running.
 */
final class WindowMenuBar extends JMenuBar {

  private static final SimulationRate INITIAL_SIMULATION_RATE = SimulationRate.NORMAL;

  private SimulationRate simulationRate = INITIAL_SIMULATION_RATE;

  private WindowMenuBar() {}

  /** Returns the currently selected {@link SimulationRate}. */
  SimulationRate selectedSimulationRate() {
    return simulationRate;
  }

  /** Adds a menu to this menu bar that allows the user to change the simulation rate. */
  private WindowMenuBar addSimulationRateMenu() {
    JMenu menu = new JMenu("Rate");
    menu.setMnemonic(KeyEvent.VK_R);

    ButtonGroup group = new ButtonGroup();
    for (SimulationRate rate : SimulationRate.values()) {
      JRadioButtonMenuItem item = new JRadioButtonMenuItem(rate.description());
      item.setSelected(rate.ordinal() == simulationRate.ordinal());
      item.setAccelerator(getKeyStroke(rate.ordinal() + KeyEvent.VK_1, InputEvent.ALT_MASK));
      item.addActionListener(event -> simulationRate = rate);

      group.add(item);
      menu.add(item);
    }

    add(menu);
    return this;
  }

  /** Creates a new {@link WindowMenuBar}. */
  static WindowMenuBar create() {
    return new WindowMenuBar().addSimulationRateMenu();
  }
}
