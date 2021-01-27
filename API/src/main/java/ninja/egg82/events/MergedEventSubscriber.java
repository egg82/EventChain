package ninja.egg82.events;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event subscriber that can support
 * multiple events of a similar superclass type.
 *
 * @param <E> the base event class type
 * @param <T> the superclass type of the events to merge
 */
public interface MergedEventSubscriber<E, T> extends EventSubscriber<T> {
    @NotNull MergedEventSubscriber<E, T> bind(@NotNull Class<E> event, @NotNull Function<E, T> function);

    void callMerged(@NotNull Object event) throws EventException;
}
