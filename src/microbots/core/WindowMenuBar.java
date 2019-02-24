package microbots.core;

import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import microbots.impl.ScrapPile;

/**
 * The menu bar offers options for controlling the running simulation. For example, one can change
 * how quickly the simulation is running.
 */
final class WindowMenuBar extends JMenuBar {

  private static final SimulationRate INITIAL_SIMULATION_RATE = SimulationRate.NORMAL;

  private SimulationRate simulationRate = INITIAL_SIMULATION_RATE;

  private final Window window;

  private WindowMenuBar(Window window) {
    this.window = window;
  }

  /** Returns the currently selected {@link SimulationRate}. */
  SimulationRate selectedSimulationRate() {
    return simulationRate;
  }

  /**
   * Adds a menu to this menu bar that allows the user to select various settings of the simulation,
   * such as map and participants, and start a new simulation.
   */
  private WindowMenuBar addSimulationSettingsMenu() {
    JMenu menu = new JMenu("Simulation");
    menu.setMnemonic(KeyEvent.VK_S);

    menu.add(createRunItem());
    menu.addSeparator();

    menu.add(createExitItem());

    add(menu);
    return this;
  }

  /** Creates the menu item that runs a new simulation when selected. */
  private JMenuItem createRunItem() {
    JMenuItem runItem = new JMenuItem("Run", KeyEvent.VK_R);
    runItem.setAccelerator(getKeyStroke(KeyEvent.VK_F5, 0));
    runItem.addActionListener(
        event -> {
          try {
            window.setSimulation(Simulation.builder().addMpuType(ScrapPile.class).build());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
    return runItem;
  }

  /** Creates the menu item that exits the game when selected. */
  private JMenuItem createExitItem() {
    JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
    exitItem.addActionListener(event -> System.exit(0));
    return exitItem;
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
  static WindowMenuBar create(Window window) {
    return new WindowMenuBar(window).addSimulationSettingsMenu().addSimulationRateMenu();
  }
}
