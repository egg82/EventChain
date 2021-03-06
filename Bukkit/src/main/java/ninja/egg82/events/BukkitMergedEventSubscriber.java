package ninja.egg82.events;

import ninja.egg82.events.internal.BukkitHandlerMapping;
import org.bukkit.event.EventException;
import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BukkitMergedEventSubscriber<E1 extends Event, T> extends AbstractMergedPriorityEventSubscriber<BukkitMergedEventSubscriber<E1, T>, EventPriority, E1, T> implements Listener {
    private final Plugin plugin;

    public BukkitMergedEventSubscriber(@NotNull Plugin plugin, @NotNull Class<T> superclass) {
        super(superclass);

        this.plugin = plugin;
    }

    @Override
    public @NotNull BukkitMergedEventSubscriber<E1, T> bind(@NotNull Class<E1> event, @NotNull EventPriority priority, @NotNull Function<E1, T> function) {
        mappings.put(event, new BukkitHandlerMapping<>(priority, function));

        plugin.getServer().getPluginManager().registerEvent(event, this, priority, (l, e) -> {
            try {
                callMerged(e, priority);
            } catch (PriorityEventException ex) {
                throw new EventException(ex, "Could not call merged event subscriber.");
            }
        }, plugin, false);

        return this;
    }

    @Override
    public void cancel() {
        super.cancel();
        HandlerList.unregisterAll(this);
    }
}
