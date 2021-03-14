package ninja.egg82.events.internal;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class KyoriHandlerMapping<E, T> extends AbstractPriorityHandlerMapping<Integer, E, T> {
    public KyoriHandlerMapping(int priority, @NotNull Function<E, T> function) {
        super(priority, function);
    }
}
