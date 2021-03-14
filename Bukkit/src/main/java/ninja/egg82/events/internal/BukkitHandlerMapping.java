package ninja.egg82.events.internal;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BukkitHandlerMapping<E extends Event, T> extends AbstractPriorityHandlerMapping<EventPriority, E, T> {
    public BukkitHandlerMapping(@NotNull EventPriority priority, @NotNull Function<E, T> function) {
        super(priority, function);
    }
}
