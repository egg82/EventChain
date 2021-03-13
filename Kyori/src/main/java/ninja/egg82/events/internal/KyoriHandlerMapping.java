package ninja.egg82.events.internal;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class KyoriHandlerMapping<E, T> extends AbstractPriorityHandlerMapping<Integer, E, T> {
    public KyoriHandlerMapping(int priority, @NotNull Function<E, T> function) {
        super(priority, function);
    }
}
