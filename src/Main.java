import microbots.core.Simulation;
import microbots.impl.ScrapPile;

public final class Main {
  public static void main(String[] args) {
    Simulation.builder()
        .addMpuType(ScrapPile.class)
        .start();
  }
}
