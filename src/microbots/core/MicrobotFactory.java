package microbots.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import microbots.Direction;
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
 *           .ofEachMpu(Microbot9000.class, MicrobotPrime.class);
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
  ImmutableList<Microbot> ofEachMpu(Class<? extends MicrobotProcessingUnit>... mpuTypes)
      throws Exception {
    checkNotNull(mpuTypes);
    ArrayList<Microbot> microbots = new ArrayList<>(quantity * mpuTypes.length);
    for (Class<? extends MicrobotProcessingUnit> mpuType : mpuTypes) {
      microbots.addAll(ofMpu(mpuType));
    }
    Collections.shuffle(microbots);
    return ImmutableList.copyOf(microbots);
  }

  /**
   * Creates {@link #quantity} microbots each with the given {@link MicrobotProcessingUnit mpuType}.
   */
  private <MpuT extends MicrobotProcessingUnit> ArrayList<Microbot> ofMpu(Class<MpuT> mpuType)
      throws Exception {
    checkNotNull(mpuType);
    ArrayList<Microbot> microbots = new ArrayList<>(quantity);
    Constructor<MpuT> constructor = mpuType.getConstructor();
    for (int i = 0; i < quantity; i++) {
      MpuT mpu = constructor.newInstance();
      Direction facing = Direction.random();
      microbots.add(new Microbot(mpu, facing));
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
