package ninja.egg82.events;

import java.util.function.Consumer;
import org.spongepowered.api.event.Cancellable;

/**
 * This class stolen from Luck's Helper @ https://github.com/lucko/helper/blob/master/helper/src/main/java/me/lucko/helper/event/filter/EventHandlers.java
 */
public class SpongeEventHandlers {
    private SpongeEventHandlers() { }

    private static final Consumer<? extends Cancellable> SET_CANCELLED = e -> e.setCancelled(true);
    private static final Consumer<? extends Cancellable> UNSET_CANCELLED = e -> e.setCancelled(false);

    /**
     * Returns a consumer which cancels the event
     *
     * @param <T> the event type
     * @return a consumer which cancels the event
     */
    public static <T extends Cancellable> Consumer<T> cancel() { return (Consumer<T>) SET_CANCELLED; }

    /**
     * Returns a consumer which un-cancels the event
     *
     * @param <T> the event type
     * @return a consumer which un-cancels the event
     */
    public static <T extends Cancellable> Consumer<T> uncancel() { return (Consumer<T>) UNSET_CANCELLED; }
}
