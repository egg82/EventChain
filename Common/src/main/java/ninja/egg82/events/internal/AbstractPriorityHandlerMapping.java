package ninja.egg82.events.internal;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPriorityHandlerMapping<P, E, T> extends AbstractHandlerMapping<E, T> implements PriorityHandlerMapping<P, E, T> {
    private final P priority;

    protected AbstractPriorityHandlerMapping(@NotNull P priority, @NotNull Function<? extends E, T> function) {
        super(function);
        this.priority = priority;
    }

    public @NotNull P getPriority() { return priority; }
}
