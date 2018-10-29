package ninja.egg82.events.internal;

import java.util.function.Function;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Order;

public class SpongeHandlerMapping<T> {
    private Order order;
    private Function<Event, T> function;

    public SpongeHandlerMapping(Order order, Function<Event, T> function) {
        this.order = order;
        this.function = function;
    }

    public Function<Event, T> getFunction() { return function; }

    public Order getOrder() { return order; }
}
