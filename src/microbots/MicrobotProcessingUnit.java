package microbots;

/** A Microbot Processing Unit (MPU) acts as the brain of a microbot. */
public abstract class MicrobotProcessingUnit {

  /** Default constructor to ensure subclasses have a zero-parameter constructor. */
  public MicrobotProcessingUnit() {}

  /**
   * Returns the name of the microbot. Defaults to the class name, but may be overridden by
   * subclasses.
   */
  public String name() {
    return getClass().getSimpleName();
  }

  /**
   * Given a microbot's {@link Surroundings}, returns the {@link Action} this microbot will attempt
   * to take.
   */
  public abstract Action getAction(Surroundings surroundings);
}
