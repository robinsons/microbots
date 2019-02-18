import microbots.core.Simulation;
import microbots.core.VictoryCondition;
import microbots.impl.HiveBot;
import microbots.impl.JunkyardBot;
import microbots.impl.ScrapPile;

public final class Main {
  public static void main(String[] args) throws Exception {
    VictoryCondition oneMinuteOr80PercentDominance =
        VictoryCondition.elapsedTime(60000L).or(VictoryCondition.populationThreshold(0.8d));
    Simulation simulation =
        Simulation.builder()
            .setPopulationSize(300)
            .setVictoryCondition(oneMinuteOr80PercentDominance)
            .addMpuType(ScrapPile.class)
            .addMpuType(JunkyardBot.class)
            .addMpuType(HiveBot.class)
            .build();
    simulation.run();
  }
}
