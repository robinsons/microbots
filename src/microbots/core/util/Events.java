package microbots.core.util;

import com.google.common.eventbus.EventBus;

/** Utility class to coordinate events between the model and UI via an {@link EventBus}. */
public final class Events {

  private static final EventBus EVENT_BUS = new EventBus();

  // Not intended for instantiation.
  private Events() {}

  /** @see EventBus#register(Object) */
  public static void register(Object object) {
    EVENT_BUS.register(object);
  }

  /** @see EventBus#unregister(Object) */
  public static void unregister(Object object) {
    EVENT_BUS.unregister(object);
  }

  /** @see EventBus#post(Object) */
  public static void post(Event event) {
    EVENT_BUS.post(event);
  }
}
