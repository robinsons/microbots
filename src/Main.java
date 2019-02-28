import microbots.core.Simulation;
import microbots.impl.HiveBot;
import microbots.impl.JunkyardBot;
import microbots.impl.ScrapPile;

public final class Main {
  public static void main(String[] args) {
    Simulation.builder()
        .addMpuType(ScrapPile.class)
        .addMpuType(JunkyardBot.class)
        .addMpuType(HiveBot.class)
        .start();
  }
}
