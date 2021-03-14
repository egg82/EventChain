package ninja.egg82.events;

import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.egg82.events.internal.BungeeAllEventsListener;
import ninja.egg82.events.internal.BungeeHandlerMapping;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BungeeMergedEventSubscriber<E1 extends Event, T> extends AbstractMergedPriorityEventSubscriber<BungeeMergedEventSubscriber<E1, T>, Byte, E1, T> {
    private final Plugin plugin;
    private final BungeeAllEventsListener<E1> listener;

    public BungeeMergedEventSubscriber(@NotNull Plugin plugin, @NotNull Class<T> superclass, byte priority) {
        super(superclass);

        this.plugin = plugin;

        listener = new BungeeAllEventsListener<>(this, priority);
        plugin.getProxy().getPluginManager().registerListener(plugin, listener);
    }

    @Override
    @Deprecated
    public @NotNull BungeeMergedEventSubscriber<E1, T> bind(@NotNull Class<E1> event, @NotNull Byte priority, @NotNull Function<E1, T> function) {
        return bind(event, priority.byteValue(), function);
    }

    public @NotNull BungeeMergedEventSubscriber<E1, T> bind(@NotNull Class<E1> event, byte priority, @NotNull Function<E1, T> function) {
        mappings.put(event, new BungeeHandlerMapping<>(priority, function));
        return this;
    }

    @Override
    public void cancel() {
        super.cancel();
        plugin.getProxy().getPluginManager().unregisterListener(listener);
    }
}
