package microbots.core;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Color;
import java.lang.reflect.Constructor;
import microbots.Action;
import microbots.MicrobotProcessingUnit;
import microbots.Obstacle;
import microbots.Surroundings;

/** Wraps a {@link MicrobotProcessingUnit} along with additional data for the simulation. */
final class Microbot {

  private MicrobotProcessingUnit mpu;
  private Direction facing;
  private int row;
  private int column;

  Microbot(MicrobotProcessingUnit mpu, Direction facing) {
    this.mpu = checkNotNull(mpu);
    this.facing = checkNotNull(facing);

    // These will be initialized when the microbot is placed in an arena.
    this.row = -1;
    this.column = -1;
  }

  /** @see MicrobotProcessingUnit#name() */
  String name() {
    return firstNonNull(mpu.name(), mpu.getClass().getSimpleName());
  }

  /** @see MicrobotProcessingUnit#color() */
  Color color() {
    return firstNonNull(mpu.color(), Color.WHITE);
  }

  /** @see MicrobotProcessingUnit#getAction(Surroundings) */
  Action getAction(Surroundings surroundings) {
    checkNotNull(surroundings);
    return firstNonNull(mpu.getAction(surroundings), Action.WAIT);
  }

  /** Returns the row this microbot is located at in the arena. */
  int row() {
    return row;
  }

  /** Returns the column this microbot is located at in the arena. */
  int column() {
    return column;
  }

  /** Updates this microbot's {@link #row} and {@link #column}. */
  void setPosition(int row, int column) {
    this.row = row;
    this.column = column;
  }

  /** Returns the {@link Direction} this microbot is currently facing. */
  Direction facing() {
    return facing;
  }

  /** Rotates this microbot 90 degrees counterclockwise. */
  void rotateLeft() {
    facing = facing.clockwise270();
  }

  /** Rotates this microbot 90 degrees clockwise. */
  void rotateRight() {
    facing = facing.clockwise90();
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
    return other.mpu.getClass().equals(this.mpu.getClass()) ? Obstacle.FRIEND : Obstacle.ENEMY;
  }
}
