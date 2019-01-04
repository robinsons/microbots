package microbots;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A microbot's surroundings are the four cells adjacent to it in the cardinal directions. When
 * preparing to take an action, a microbot may inspect its surroundings to influence its decision.
 */
public final class Surroundings {

  private final Obstacle front;
  private final Obstacle left;
  private final Obstacle right;
  private final Obstacle back;

  public Surroundings(Obstacle front, Obstacle left, Obstacle right, Obstacle back) {
    this.front = checkNotNull(front);
    this.left = checkNotNull(left);
    this.right = checkNotNull(right);
    this.back = checkNotNull(back);
  }

  /** The {@link Obstacle} immediately in front of the microbot. */
  public Obstacle front() {
    return front;
  }

  /** The {@link Obstacle} on the microbot's left. */
  public Obstacle left() {
    return left;
  }

  /** The {@link Obstacle} on the microbot's right. */
  public Obstacle right() {
    return right;
  }

  /** The {@link Obstacle} behind the microbot. */
  public Obstacle back() {
    return back;
  }
}
