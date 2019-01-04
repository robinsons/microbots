package microbots;

import java.awt.Color;

/** A Microbot Processing Unit (MPU) acts as the brain of a microbot. */
public abstract class MicrobotProcessingUnit {

  private final String simpleName = getClass().getSimpleName();

  /** Default constructor to ensure subclasses have a zero-parameter constructor. */
  public MicrobotProcessingUnit() {}

  /**
   * Returns the name of this microbot. Defaults to the class name, but may be overridden by
   * subclasses. If null is returned, will use the class name.
   */
  public String name() {
    return simpleName;
  }

  /**
   * Returns the color to use when drawing this microbot. If null is returned, will use {@link
   * Color#WHITE}.
   */
  public abstract Color color();

  /**
   * Given a microbot's {@link Surroundings}, returns the {@link Action} this microbot will attempt
   * to take. A return value of null is interpreted as {@link Action#WAIT}.
   */
  public abstract Action getAction(Surroundings surroundings);
}
