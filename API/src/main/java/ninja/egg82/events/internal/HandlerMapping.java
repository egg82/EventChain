package ninja.egg82.events.internal;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Represents a mapping function handler.
 *
 * <p>This converts an {@link Object} to the type {@code <T>}</p>
 *
 * @param <T> the type to convert to
 */
public interface HandlerMapping<T> {
    /**
     * Gets the mapping function.
     *
     * @return the mapping function
     */
    @NotNull Function<Object, T> getFunction();
}
