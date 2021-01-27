package ninja.egg82.events.internal;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public interface HandlerMapping<T> {
    @NotNull Function<Object, T> getFunction();
}
