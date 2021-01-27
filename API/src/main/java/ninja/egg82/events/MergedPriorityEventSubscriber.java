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
    @NotNull MergedPriorityEventSubscriber<P, E, T> bind(@NotNull Class<E> event, @NotNull P priority, @NotNull Function<E, T> function);

    void callMerged(@NotNull Object event, @NotNull P priority) throws PriorityEventException;
}
