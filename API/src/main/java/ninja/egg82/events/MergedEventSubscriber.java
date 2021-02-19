package ninja.egg82.events;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event subscriber that can support
 * multiple events of a similar superclass type.
 *
 * @param <S> the class type of the subscriber
 * @param <E> the base event class type
 * @param <T> the superclass type of the events to merge
 */
public interface MergedEventSubscriber<S extends MergedEventSubscriber<S, E, T>, E, T> extends EventSubscriber<S, T> {
    /**
     * Binds a new event class to the merged subscriber.
     *
     * @param event the known event to handle
     * @param function the function used to translate handled event objects into a known event to handle
     * @return this {@link MergedEventSubscriber} instance (for chaining)
     * @throws NullPointerException if the {@code event} or {@code function} is null
     */
    @NotNull S bind(@NotNull Class<E> event, @NotNull Function<E, T> function);

    /**
     * Runs an event through this subscriber chain.
     *
     * @param event The event to call
     * @throws EventException if an exception was thrown in the chain
     * @throws NullPointerException if the {@code event} is null
     */
    void callMerged(@NotNull Object event) throws EventException;
}
