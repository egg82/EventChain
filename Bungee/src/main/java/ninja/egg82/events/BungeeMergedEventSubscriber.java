package ninja.egg82.events;

import java.util.function.*;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.egg82.events.internal.BungeeAllEventsListener;
import ninja.egg82.events.internal.BungeeHandlerMapping;
import org.jetbrains.annotations.NotNull;

public class BungeeMergedEventSubscriber<E1 extends Event, T> extends AbstractMergedPriorityEventSubscriber<Byte, E1, T> {
    private final Plugin plugin;
    private final BungeeAllEventsListener<E1> listener;

    public BungeeMergedEventSubscriber(@NotNull Plugin plugin, @NotNull Class<T> commonClass, byte priority) {
        super(commonClass);

        this.plugin = plugin;

        listener = new BungeeAllEventsListener<>(this, priority);
        plugin.getProxy().getPluginManager().registerListener(plugin, listener);
    }

    public @NotNull MergedPriorityEventSubscriber<Byte, E1, T> bind(@NotNull Class<E1> event, @NotNull Byte priority, @NotNull Function<E1, T> function) {
        mappings.put(event, new BungeeHandlerMapping<>(priority, function));
        return this;
    }

    public void cancel() {
        super.cancel();
        plugin.getProxy().getPluginManager().unregisterListener(listener);
    }
}
