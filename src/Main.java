import microbots.core.model.Simulation;
import microbots.impl.Hive;
import microbots.impl.Microbot9000;
import microbots.impl.ScrapPile;

public final class Main {
  public static void main(String[] args) {
    Simulation.builder()
        .setPopulationSize(600)
        .addMpuType(ScrapPile.class)
        .addMpuType(Hive.class)
        .addMpuType(Microbot9000.class)
//        .addMpuType(Looper1.class)
//        .addMpuType(Looper2.class)
//        .addMpuType(Sweeper1.class)
//        .addMpuType(Sweeper2.class)
        .start();
  }
}
