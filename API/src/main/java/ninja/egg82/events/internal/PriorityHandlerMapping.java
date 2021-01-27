package ninja.egg82.events.internal;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a mapping function handler with
 * a priority.
 *
 * <p>This converts an {@link Object} to the type {@code <T>}</p>
 *
 * @param <P> the priority class type
 * @param <T> the type to convert to
 */
public interface PriorityHandlerMapping<P, T> extends HandlerMapping<T> {
    /**
     * Gets the priority of the current mapping function handler.
     *
     * @return the priority
     */
    @NotNull P getPriority();
}
