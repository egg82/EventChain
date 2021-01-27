package ninja.egg82.events.internal;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractHandlerMapping<E, T> implements HandlerMapping<T> {
    private final Function<Object, T> function;

    protected AbstractHandlerMapping(@NotNull Function<E, T> function) {
        this.function = o -> function.apply((E) o);
    }

    public @NotNull Function<Object, T> getFunction() { return function; }
}
