package ninja.egg82.events;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Order;

public class SpongeEventSubscriber<T extends Event> extends AbstractPriorityEventSubscriber<Order, T> {
    private final EventListener<? super T> listener;

    public SpongeEventSubscriber(@NotNull Object plugin, @NotNull Class<T> event, @NotNull Order priority, boolean beforeModifications) {
        super(event);

        listener = e -> {
            if (!baseClass.isInstance(e)) {
                return;
            }

            try {
                call(e, priority);
            } catch (PriorityEventException ex) {
                throw new RuntimeException("Could not call event subscriber.", ex);
            }
        };

        Sponge.getEventManager().registerListener(plugin, event, priority, beforeModifications, listener);
    }

    public void cancel() {
        super.cancel();
        Sponge.getEventManager().unregisterListeners(listener);
    }
}
