package ninja.egg82.events.internal;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Order;

import java.util.function.Function;

public class SpongeHandlerMapping<E extends Event, T> extends AbstractPriorityHandlerMapping<Order, E, T> {
    public SpongeHandlerMapping(@NotNull Order priority, @NotNull Function<E, T> function) {
        super(priority, function);
    }
}
