package ninja.egg82.events.internal;

import com.velocitypowered.api.event.PostOrder;
import java.util.function.Function;

public class VelocityHandlerMapping<T> {
    private PostOrder order;
    private Function<Object, T> function;

    public VelocityHandlerMapping(PostOrder order, Function<Object, T> function) {
        this.order = order;
        this.function = function;
    }

    public Function<Object, T> getFunction() { return function; }

    public PostOrder getOrder() { return order; }
}
