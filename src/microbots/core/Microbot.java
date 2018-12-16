package microbots.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Constructor;
import microbots.Action;
import microbots.Direction;
import microbots.MicrobotProcessingUnit;
import microbots.Obstacle;
import microbots.Surroundings;

/** Wraps a {@link MicrobotProcessingUnit} along with additional data for the simulation. */
final class Microbot {

  private MicrobotProcessingUnit mpu;
  private Direction facing;

  Microbot(MicrobotProcessingUnit mpu, Direction facing) {
    this.mpu = checkNotNull(mpu);
    this.facing = checkNotNull(facing);
  }

  /** @see MicrobotProcessingUnit#name() */
  String name() {
    return mpu.name();
  }

  /** @see MicrobotProcessingUnit#getAction(Surroundings) */
  Action getAction(Surroundings surroundings) {
    checkNotNull(surroundings);
    return mpu.getAction(surroundings);
  }

  /** Returns the {@link Direction} this microbot is currently facing. */
  Direction facing() {
    return facing;
  }

  /** Rotates this microbot 90 degrees counterclockwise. */
  void rotateLeft() {
    facing = facing.previous();
  }

  /** Rotates this microbot 90 degrees clockwise. */
  void rotateRight() {
    facing = facing.next();
  }

  /**
   * Attempts to hack the specified microbot, converting their {@link #mpu} into one with the same
   * type as this microbot's. If {@code other} already has the same MPU type, then nothing happens.
   */
  void hack(Microbot other) {
    checkNotNull(other);
    if (classify(other) == Obstacle.ENEMY) {
      try {
        Constructor<? extends MicrobotProcessingUnit> constructor = mpu.getClass().getConstructor();
        other.mpu = constructor.newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Returns the {@link Obstacle obstacle classification} of the specified microbot relative to this
   * microbot. In other words, returns whether the microbots are of the same type or not.
   */
  Obstacle classify(Microbot other) {
    checkNotNull(other);
    return other.mpu.getClass().equals(this.mpu.getClass())
        ? Obstacle.FRIEND
        : Obstacle.ENEMY;
  }
}
