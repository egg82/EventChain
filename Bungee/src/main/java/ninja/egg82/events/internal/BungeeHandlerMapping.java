package ninja.egg82.events.internal;

import java.util.function.Function;
import net.md_5.bungee.api.plugin.Event;
import org.jetbrains.annotations.NotNull;

public class BungeeHandlerMapping<E extends Event, T> extends AbstractPriorityHandlerMapping<Byte, E, T> {
    public BungeeHandlerMapping(byte priority, @NotNull Function<E, T> function) {
        super(priority, function);
    }
}
