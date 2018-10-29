package ninja.egg82.events.internal;

import java.util.function.Function;
import net.md_5.bungee.api.plugin.Event;

public class BungeeHandlerMapping<T> {
    private byte priority;
    private Function<Event, T> function;

    public BungeeHandlerMapping(byte priority, Function<Event, T> function) {
        this.priority = priority;
        this.function = function;
    }

    public Function<Event, T> getFunction() { return function; }

    public byte getPriority() { return priority; }
}
