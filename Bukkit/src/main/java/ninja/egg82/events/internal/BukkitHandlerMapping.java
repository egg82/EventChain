package ninja.egg82.events.internal;

import java.util.function.Function;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public class BukkitHandlerMapping<T> {
    private EventPriority priority;
    private Function<Event, T> function;

    public BukkitHandlerMapping(EventPriority priority, Function<Event, T> function) {
        this.priority = priority;
        this.function = function;
    }

    public Function<Event, T> getFunction() { return function; }

    public EventPriority getPriority() { return priority; }
}
