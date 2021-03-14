package ninja.egg82.events.internal;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class AbstractHandlerMapping<E, T> implements HandlerMapping<T> {
    private final Function<Object, T> function;

    @SuppressWarnings("unchecked")
    protected AbstractHandlerMapping(@NotNull Function<E, T> function) {
        this.function = o -> function.apply((E) o);
    }

    @Override
    public @NotNull Function<Object, T> getFunction() { return function; }
}
