package ninja.egg82.events;

import net.md_5.bungee.api.plugin.Cancellable;

import java.util.function.Predicate;

/**
 * This class stolen from Luck's Helper @ https://github.com/lucko/helper/blob/master/helper/src/main/java/me/lucko/helper/event/filter/EventFilters.java
 */
public class BungeeEventFilters {
    private BungeeEventFilters() {
    }

    private static final Predicate<? extends Cancellable> IGNORE_CANCELLED = e -> !e.isCancelled();
    private static final Predicate<? extends Cancellable> IGNORE_UNCANCELLED = Cancellable::isCancelled;

    /**
     * Returns a predicate which only returns true if the event isn't cancelled
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the event isn't cancelled
     */
    public static <T extends Cancellable> Predicate<T> ignoreCancelled() {
        return (Predicate<T>) IGNORE_CANCELLED;
    }

    /**
     * Returns a predicate which only returns true if the event is cancelled
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the event is cancelled
     */
    public static <T extends Cancellable> Predicate<T> ignoreNotCancelled() {
        return (Predicate<T>) IGNORE_UNCANCELLED;
    }
}
