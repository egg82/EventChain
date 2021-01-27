package ninja.egg82.events;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a priority-driven event subscriber that can support
 * multiple events of a similar superclass type.
 *
 * <p>Additionally, this event supports event systems
 * that use an event priority system.</p>
 *
 * @param <P> the class type of the priority system
 * @param <E> the base event class type
 * @param <T> the superclass type of the events to merge
 */
public interface MergedPriorityEventSubscriber<P, E, T> extends PriorityEventSubscriber<P, T> {
    /**
     * Binds a new event class to the merged subscriber.
     *
     * @param event the known event to handle
     * @param priority the priority of the event
     * @param function the function used to translate handled event objects into a known event to handle
     * @return this {@link MergedEventSubscriber} instance (for chaining)
     * @throws NullPointerException if the {@code event} or {@code function} is null
     */
    @NotNull MergedPriorityEventSubscriber<P, E, T> bind(@NotNull Class<E> event, @NotNull P priority, @NotNull Function<E, T> function);

    /**
     * Runs an event through this subscriber chain.
     *
     * @param event The event to call
     * @param priority The event priority
     * @throws PriorityEventException if an exception was thrown in the chain
     * @throws NullPointerException if the {@code event} is null
     */
    void callMerged(@NotNull Object event, @NotNull P priority) throws PriorityEventException;
}