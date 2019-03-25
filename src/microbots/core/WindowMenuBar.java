package microbots.core;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static javax.swing.KeyStroke.getKeyStroke;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.Reflection;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.IntStream;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import microbots.MicrobotProcessingUnit;
import microbots.core.Events.SimulationRateChangedEvent;

/**
 * The menu bar offers options for controlling the running simulation. For example, one can change
 * how quickly the simulation is running.
 */
final class WindowMenuBar extends JMenuBar {

  private static final int MINIMUM_POPULATION_SIZE = 100;
  private static final int MAXIMUM_POPULATION_SIZE = 1000;
  private static final int POPULATION_SIZE_STEP = 100;

  private SimulationRate simulationRate = SimulationDefaults.SIMULATION_RATE;
  private ArenaMap arenaMap = SimulationDefaults.ARENA_MAP;
  private int populationSize;

  private final Collection<Class<? extends MicrobotProcessingUnit>> microbotTypes;

  private WindowMenuBar(
      Collection<Class<? extends MicrobotProcessingUnit>> microbotTypes, int populationSize) {
    this.microbotTypes = microbotTypes;
    this.populationSize = populationSize;
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
    menu.add(createArenaMapSubMenu());
    menu.add(createMicrobotSelectionSubMenu());
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
        event ->
            Simulation.builder()
                .setPopulationSize(populationSize)
                .setArenaMap(arenaMap)
                .setSimulationRate(simulationRate)
                .addMpuTypes(ImmutableSet.copyOf(microbotTypes))
                .startInternal());
    return item;
  }

  /** Creates the sub menu that allows for specifying the population size of new simulations. */
  private JMenu createPopulationSizeSubMenu() {
    JMenu menu = new JMenu("Population Size");
    menu.setMnemonic(KeyEvent.VK_P);

    IntStream.Builder populationSizes = IntStream.builder();
    populationSizes.accept(populationSize);
    for (int i = MINIMUM_POPULATION_SIZE; i <= MAXIMUM_POPULATION_SIZE; i += POPULATION_SIZE_STEP) {
      populationSizes.accept(i);
    }

    ButtonGroup group = new ButtonGroup();
    populationSizes
        .build()
        .distinct()
        .sorted()
        .forEach(
            populationSize -> {
              JRadioButtonMenuItem item =
                  new JRadioButtonMenuItem(String.format("%d", populationSize));
              item.setSelected(populationSize == this.populationSize);
              item.addActionListener(event -> this.populationSize = populationSize);

              group.add(item);
              menu.add(item);
            });

    return menu;
  }

  /** Creates the sub menu that allows for specifying the desired map of new simulations. */
  private JMenu createArenaMapSubMenu() {
    JMenu menu = new JMenu("Map");
    menu.setMnemonic(KeyEvent.VK_M);

    ButtonGroup group = new ButtonGroup();
    for (ArenaMap map : ArenaMap.values()) {
      JRadioButtonMenuItem item = new JRadioButtonMenuItem(map.description());
      item.setMnemonic(map.mnemonic());
      item.setSelected(map.ordinal() == arenaMap.ordinal());
      item.addActionListener(event -> arenaMap = map);

      group.add(item);
      menu.add(item);
    }

    return menu;
  }

  /**
   * Creates the sub menu that allows for toggling individual microbot types to participate in the
   * simulation.
   */
  private JMenu createMicrobotSelectionSubMenu() {
    JMenu menu = new JMenu("Microbots");
    menu.setMnemonic(KeyEvent.VK_B);

    addMicrobotTypeSection(menu);
    menu.addSeparator();
    addSelectAllAndDeselectAllSection(menu);

    return menu;
  }

  /**
   * Adds menu items to allow for selecting or deselecting all other menu items in the given menu.
   */
  private void addSelectAllAndDeselectAllSection(JMenu menu) {
    JMenuItem selectAll = new JMenuItem("Select All", KeyEvent.VK_A);
    JMenuItem deselectAll = new JMenuItem("Deselect All", KeyEvent.VK_D);
    ActionListener selectionActionListener =
        event -> {
          for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem item = menu.getItem(i);
            if (item != null && item != selectAll && item != deselectAll) {
              item.setSelected(selectAll.equals(event.getSource()));
            }
          }
        };

    selectAll.addActionListener(selectionActionListener);
    deselectAll.addActionListener(selectionActionListener);

    menu.add(selectAll);
    menu.add(deselectAll);
  }

  /** Adds menu items that allow the user to toggle individual microbot types. */
  private void addMicrobotTypeSection(JMenu menu) {
    ImmutableList<Class<? extends MicrobotProcessingUnit>> microbotTypes = fetchMicrobotTypes();
    for (Class<? extends MicrobotProcessingUnit> microbotType : microbotTypes) {
      JCheckBoxMenuItem item = new JCheckBoxMenuItem(microbotType.getSimpleName());
      item.setSelected(WindowMenuBar.this.microbotTypes.contains(microbotType));
      item.addItemListener(
          event -> {
            if (((JCheckBoxMenuItem) event.getItem()).isSelected()) {
              WindowMenuBar.this.microbotTypes.add(microbotType);
            } else {
              WindowMenuBar.this.microbotTypes.remove(microbotType);
            }
          });
      menu.add(item);
    }
  }

  @SuppressWarnings("unchecked") // Cast is safe because we check isAssignableFrom first.
  private static ImmutableList<Class<? extends MicrobotProcessingUnit>> fetchMicrobotTypes() {
    try {
      String packageName = Reflection.getPackageName(MicrobotProcessingUnit.class);
      ClassPath classPath = ClassPath.from(MicrobotProcessingUnit.class.getClassLoader());
      return classPath
          .getTopLevelClassesRecursive(packageName)
          .stream()
          .map(ClassInfo::load)
          .filter(Objects::nonNull)
          .filter(MicrobotProcessingUnit.class::isAssignableFrom)
          .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
          .map(clazz -> (Class<? extends MicrobotProcessingUnit>) clazz)
          .sorted(Comparator.comparing(Class::getSimpleName))
          .collect(toImmutableList());
    } catch (Exception e) {
      throw new RuntimeException("Failed to load microbot types.", e);
    }
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
      item.setSelected(rate.ordinal() == SimulationDefaults.SIMULATION_RATE.ordinal());
      item.setAccelerator(getKeyStroke(rate.ordinal() + KeyEvent.VK_1, InputEvent.ALT_MASK));
      item.addActionListener(
          event -> {
            simulationRate = rate;
            Events.post(new SimulationRateChangedEvent(rate));
          });

      group.add(item);
      menu.add(item);
    }

    add(menu);
    return this;
  }

  /** Creates a new {@link WindowMenuBar}. */
  static WindowMenuBar create(
      Collection<Class<? extends MicrobotProcessingUnit>> selectedMpuTypes, int populationSize) {
    return new WindowMenuBar(selectedMpuTypes, populationSize)
        .addSimulationSettingsMenu()
        .addSimulationRateMenu();
  }
}
