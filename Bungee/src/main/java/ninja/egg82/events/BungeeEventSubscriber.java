package ninja.egg82.events;

import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.egg82.events.internal.BungeeAllEventsListener;
import org.jetbrains.annotations.NotNull;

public class BungeeEventSubscriber<T extends Event> extends AbstractPriorityEventSubscriber<Byte, T> {
    private final Plugin plugin;
    private final BungeeAllEventsListener<T> listener;

    public BungeeEventSubscriber(@NotNull Plugin plugin, @NotNull Class<T> event, byte priority) {
        super(event);

        this.plugin = plugin;

        listener = new BungeeAllEventsListener<>(this, priority);
        plugin.getProxy().getPluginManager().registerListener(plugin, listener);
    }

    public void cancel() {
        super.cancel();
        plugin.getProxy().getPluginManager().unregisterListener(listener);
    }
}
