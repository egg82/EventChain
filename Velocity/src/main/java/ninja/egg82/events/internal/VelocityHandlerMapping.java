package ninja.egg82.events.internal;

import com.velocitypowered.api.event.PostOrder;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class VelocityHandlerMapping<E, T>extends AbstractPriorityHandlerMapping<PostOrder, E, T> {
    public VelocityHandlerMapping(@NotNull PostOrder priority, @NotNull Function<E, T> function) {
        super(priority, function);
    }
}
