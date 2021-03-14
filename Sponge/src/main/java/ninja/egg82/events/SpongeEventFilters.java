package ninja.egg82.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.service.permission.Subject;

import java.util.function.Predicate;

/**
 * This class stolen from Luck's Helper @ https://github.com/lucko/helper/blob/master/helper/src/main/java/me/lucko/helper/event/filter/EventFilters.java
 */
public class SpongeEventFilters {
    private SpongeEventFilters() { }

    private static final Predicate<? extends Cancellable> IGNORE_CANCELLED = e -> !e.isCancelled();
    private static final Predicate<? extends Cancellable> IGNORE_UNCANCELLED = Cancellable::isCancelled;

    private static final Predicate<? extends MoveEntityEvent> IGNORE_SAME_BLOCK = e ->
            !e.getFromTransform().getPosition().floor().equals(e.getToTransform().getPosition().floor());

    /**
     * Returns a predicate which only returns true if the event isn't cancelled
     *
     * @param <T> the event type
     * @return a predicate which only returns true if the event isn't cancelled
     */
    public static <T extends Cancellable> Predicate<T> ignoreCancelled() { return (Predicate<T>) IGNORE_CANCELLED; }

    /**
     * Returns a predicate which only returns true if the event is cancelled
     *
     * @param <T> the event type
     * @return a predicate which only returns true if the event is cancelled
     */
    public static <T extends Cancellable> Predicate<T> ignoreNotCancelled() { return (Predicate<T>) IGNORE_UNCANCELLED; }

    /**
     * Returns a predicate which only returns true if the player has moved over a block
     *
     * @param <T> the event type
     * @return a predicate which only returns true if the player has moved over a block
     */
    public static <T extends MoveEntityEvent> Predicate<T> ignoreSameBlock() { return (Predicate<T>) IGNORE_SAME_BLOCK; }

    /**
     * Returns a predicate which only returns true if the player has the given permission
     *
     * @param permission the permission
     * @param <T> the event type
     * @return a predicate which only returns true if the player has the given permission
     */
    public static <T extends Event> Predicate<T> playerHasPermission(String permission) { return e -> e.getCause().first(Subject.class).isPresent() && e.getCause().first(Subject.class).get().hasPermission(permission); }
}
