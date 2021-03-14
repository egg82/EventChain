package ninja.egg82.events;

import net.md_5.bungee.api.plugin.Cancellable;

import java.util.function.Predicate;

/**
 * This class stolen from Luck's Helper @ https://github.com/lucko/helper/blob/master/helper/src/main/java/me/lucko/helper/event/filter/EventFilters.java
 */
public class BungeeEventFilters {
    private BungeeEventFilters() { }

    /**
     * Returns a predicate which only returns true if the event isn't cancelled
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the event isn't cancelled
     */
    public static <T extends Cancellable> Predicate<T> ignoreCancelled() { return e -> !e.isCancelled(); }

    /**
     * Returns a predicate which only returns true if the event is cancelled
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the event is cancelled
     */
    public static <T extends Cancellable> Predicate<T> ignoreNotCancelled() { return Cancellable::isCancelled; }
}
