package microbots.core;

import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import microbots.impl.HiveBot;
import microbots.impl.JunkyardBot;
import microbots.impl.ScrapPile;

/**
 * The menu bar offers options for controlling the running simulation. For example, one can change
 * how quickly the simulation is running.
 */
final class WindowMenuBar extends JMenuBar {

  private static final SimulationRate INITIAL_SIMULATION_RATE = SimulationRate.NORMAL;
  private static final int INITIAL_POPULATION_SIZE = 300;
  private static final int MINIMUM_POPULATION_SIZE = 100;
  private static final int MAXIMUM_POPULATION_SIZE = 1000;
  private static final int POPULATION_SIZE_STEP = 100;

  private SimulationRate simulationRate = INITIAL_SIMULATION_RATE;
  private int populationSize = INITIAL_POPULATION_SIZE;

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
    menu.add(createPopulationSizeSubMenu());
    menu.addSeparator();

    menu.add(createExitItem());

    add(menu);
    return this;
  }

  /** Creates the menu item that runs a new simulation when selected. */
  private JMenuItem createRunItem() {
    JMenuItem item = new JMenuItem("Run", KeyEvent.VK_R);
    item.setAccelerator(getKeyStroke(KeyEvent.VK_F5, 0));
    item.addActionListener(
        event -> {
          try {
            window.setSimulation(
                Simulation.builder()
                    .setPopulationSize(populationSize)
                    .addMpuType(ScrapPile.class)
                    .addMpuType(JunkyardBot.class)
                    .addMpuType(HiveBot.class)
                    .build());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
    return item;
  }

  /** Creates the sub menu that allows for specifying the population size of new simulations. */
  private JMenu createPopulationSizeSubMenu() {
    JMenu menu = new JMenu("Population Size");
    menu.setMnemonic(KeyEvent.VK_P);

    ButtonGroup group = new ButtonGroup();
    for (int i = MINIMUM_POPULATION_SIZE; i <= MAXIMUM_POPULATION_SIZE; i += POPULATION_SIZE_STEP) {
      int populationSize = i;
      JRadioButtonMenuItem item = new JRadioButtonMenuItem(String.format("%d", populationSize));
      item.setSelected(populationSize == this.populationSize);
      item.addActionListener(event -> this.populationSize = populationSize);

      group.add(item);
      menu.add(item);
    }

    return menu;
  }

  /** Creates the menu item that exits the game when selected. */
  private JMenuItem createExitItem() {
    JMenuItem item = new JMenuItem("Exit", KeyEvent.VK_X);
    item.addActionListener(event -> System.exit(0));
    return item;
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
