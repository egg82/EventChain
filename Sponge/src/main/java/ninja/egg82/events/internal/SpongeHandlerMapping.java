package ninja.egg82.events.internal;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Order;

public class SpongeHandlerMapping<E extends Event, T> extends AbstractPriorityHandlerMapping<Order, E, T> {
    public SpongeHandlerMapping(@NotNull Order priority, @NotNull Function<E, T> function) {
        super(priority, function);
    }
}
