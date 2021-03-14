package ninja.egg82.events.internal;

import com.velocitypowered.api.event.PostOrder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class VelocityHandlerMapping<E, T>extends AbstractPriorityHandlerMapping<PostOrder, E, T> {
    public VelocityHandlerMapping(@NotNull PostOrder priority, @NotNull Function<E, T> function) {
        super(priority, function);
    }
}
