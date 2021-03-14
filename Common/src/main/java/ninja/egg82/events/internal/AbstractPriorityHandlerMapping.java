package ninja.egg82.events.internal;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class AbstractPriorityHandlerMapping<P, E, T> extends AbstractHandlerMapping<E, T> implements PriorityHandlerMapping<P, T> {
    private final P priority;

    protected AbstractPriorityHandlerMapping(@NotNull P priority, @NotNull Function<E, T> function) {
        super(function);
        this.priority = priority;
    }

    @Override
    public @NotNull P getPriority() { return priority; }
}
