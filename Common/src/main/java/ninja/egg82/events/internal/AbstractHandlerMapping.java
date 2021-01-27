package ninja.egg82.events.internal;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractHandlerMapping<E, T> implements HandlerMapping<E, T> {
    private final Function<? extends E, T> function;

    protected AbstractHandlerMapping(@NotNull Function<? extends E, T> function) {
        this.function = function;
    }

    public @NotNull Function<? extends E, T> getFunction() { return function; }
}
