package ninja.egg82.events.internal;

import net.md_5.bungee.api.plugin.Event;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BungeeHandlerMapping<E extends Event, T> extends AbstractPriorityHandlerMapping<Byte, E, T> {
    public BungeeHandlerMapping(byte priority, @NotNull Function<E, T> function) {
        super(priority, function);
    }
}
