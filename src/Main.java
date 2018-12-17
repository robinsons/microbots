import microbots.core.Simulation;
import microbots.impl.JunkyardBot;
import microbots.impl.ScrapPile;

public final class Main {
  public static void main(String[] args) throws Exception {
    Simulation simulation = Simulation.builder()
        .setPopulationSize(300)
        .addMpuType(ScrapPile.class)
        .addMpuType(JunkyardBot.class)
        .build();
    simulation.run();
  }
}
