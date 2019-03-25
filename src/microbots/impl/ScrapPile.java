package microbots.impl;

import java.awt.Color;
import microbots.Action;
import microbots.Direction;
import microbots.MicrobotProcessingUnit;
import microbots.Obstacle;
import microbots.State;
import microbots.Surroundings;

/**
 * Example MPU implementation for reference when creating your own bot. This bot does nothing! It
 * serves as scrap for other bots to hack.
 */
public final class ScrapPile extends MicrobotProcessingUnit {

  /**
   * You can find colors using the <a href="https://www.google.com/search?q=color+picker">Google
   * color picker widget</a>. Select a color you like, then copy its values like so:
   *
   * <ul>
   *   <li>Take the 6-digit hexadecimal value after the "#" symbol and use it in the constructor
   *       like so: #123456 -> new Color(0x123456)
   *   <li>OR, take the individual RGB values and use them in the constructor like so: rgb(50, 100,
   *       150) -> new Color(50, 100, 150)
   * </ul>
   */
  private static final Color COLOR = new Color(0xa0ccef);

  /** Override this method to give your bot its own color. */
  @Override
  public Color color() {
    return COLOR;
  }

  /**
   * Override this method to program your bot's behavior. Check out the examples in the code below
   * to get a feel for how to use the {@link State} to choose an {@link Action} for your bot.
   */
  @Override
  public Action getAction(State state) {
    // Example 1: You can look at your surroundings by writing code like this:
    Surroundings mySurroundings = state.surroundings();
    if (mySurroundings.front() == Obstacle.WALL) {
      // If you want your bot to behave a certain way when facing a wall, you can write that code
      // here.
    }

    // Example 2: You can look in a specific direction by writing code like this:
    Obstacle obstacleEastOfMe = state.obstacleInDirection(Direction.EAST);
    if (obstacleEastOfMe == Obstacle.FRIEND) {
      // If you want your bot to behave a certain way when a friend is to their east, you can write
      // that code here.
    }

    // Example 3: You can check if you are facing a particular direction by writing code like this:
    if (state.facingDirection() == Direction.SOUTH) {
      // If you want your bot to behave a certain way when it is facing south, you can write that
      // code here.
    }

    return Action.WAIT;
  }
}
