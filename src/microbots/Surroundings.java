package microbots;

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
    this.front = front;
    this.left = left;
    this.right = right;
    this.back = back;
  }

  public Obstacle front() {
    return front;
  }

  public Obstacle left() {
    return left;
  }

  public Obstacle right() {
    return right;
  }

  public Obstacle back() {
    return back;
  }
}
