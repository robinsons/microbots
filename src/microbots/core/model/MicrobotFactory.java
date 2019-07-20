package microbots.core.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import microbots.MicrobotProcessingUnit;

/**
 * Factory for creating microbots. Example usage:
 *
 * <pre>
 *   static final class Microbot9000 extends MicrobotProcesingUnit { ... }
 *   static final class MicrobotPrime extends MicrobotProcesingUnit { ... }
 *   ...
 *   ImmutableList&lt;Microbot&gt; microbots =
 *       MicrobotFactory.create(100)
 *           .ofEach(Microbot9000.class, MicrobotPrime.class);
 * </pre>
 */
final class MicrobotFactory {

  private final int quantity;

  private MicrobotFactory(int quantity) {
    this.quantity = quantity;
  }

  /**
   * For each provided {@link MicrobotProcessingUnit mpuType} creates {@link #quantity} microbots.
   * The returned list is shuffled.
   */
  ImmutableList<Microbot> ofEach(Iterable<Class<? extends MicrobotProcessingUnit>> mpuTypes) {
    checkNotNull(mpuTypes);
    ArrayList<Microbot> microbots = new ArrayList<>();
    for (Class<? extends MicrobotProcessingUnit> mpuType : mpuTypes) {
      microbots.addAll(of(mpuType));
    }
    Collections.shuffle(microbots);
    return ImmutableList.copyOf(microbots);
  }

  /**
   * Creates {@link #quantity} microbots each with the given {@link MicrobotProcessingUnit mpuType}.
   */
  private <MpuT extends MicrobotProcessingUnit> ArrayList<Microbot> of(Class<MpuT> mpuType) {
    checkNotNull(mpuType);
    ArrayList<Microbot> microbots = new ArrayList<>(quantity);
    for (int i = 0; i < quantity; i++) {
      try {
        MpuT mpu = mpuType.newInstance();
        Direction facing = Direction.random();
        microbots.add(new Microbot(mpu, facing));
      } catch (Exception e) {
        System.err.printf(
            "Encountered error constructing microbot %s; skipping this type.\n",
            mpuType.getSimpleName());
        e.printStackTrace();
        return new ArrayList<>();
      }
    }
    return microbots;
  }

  /**
   * Returns a new {@link MicrobotFactory} which can be used to create a specified quantity of
   * microbots.
   */
  static MicrobotFactory create(int quantity) {
    checkArgument(quantity >= 0, "quantity must be non-negative.");
    return new MicrobotFactory(quantity);
  }
}
