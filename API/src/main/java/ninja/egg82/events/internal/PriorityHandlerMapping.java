package ninja.egg82.events.internal;

import org.jetbrains.annotations.NotNull;

public interface PriorityHandlerMapping<P, T> extends HandlerMapping<T> {
    @NotNull P getPriority();
}
