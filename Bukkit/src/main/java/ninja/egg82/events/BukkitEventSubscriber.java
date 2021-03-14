package ninja.egg82.events;

import org.bukkit.event.EventException;
import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BukkitEventSubscriber<T extends Event> extends AbstractPriorityEventSubscriber<BukkitEventSubscriber<T>, EventPriority, T> implements Listener {
    @SuppressWarnings("unchecked")
    public BukkitEventSubscriber(@NotNull Plugin plugin, @NotNull Class<T> event, @NotNull EventPriority priority) {
        super(event);

        plugin.getServer().getPluginManager().registerEvent(event, this, priority, (l, e) -> {
            if (!event.isInstance(e)) {
                return;
            }

            try {
                call((T) e, priority);
            } catch (PriorityEventException ex) {
                throw new EventException(ex, "Could not call event subscriber.");
            }
        }, plugin, false);
    }

    @Override
    public void cancel() {
        super.cancel();
        HandlerList.unregisterAll(this);
    }
}
