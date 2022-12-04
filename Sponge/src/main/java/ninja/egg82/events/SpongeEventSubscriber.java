package ninja.egg82.events;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.Order;
import org.spongepowered.plugin.PluginContainer;

public class SpongeEventSubscriber<T extends Event> extends AbstractPriorityEventSubscriber<SpongeEventSubscriber<T>, Order, T> {
    private final EventListener<? super T> listener;

    public SpongeEventSubscriber(@NotNull PluginContainer plugin, @NotNull Class<T> event, @NotNull Order priority, boolean beforeModifications) {
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

        Sponge.eventManager().registerListener(EventListenerRegistration.builder(event).listener(listener).plugin(plugin).order(priority).beforeModifications(beforeModifications).build());
    }

    @Override
    public void cancel() {
        super.cancel();
        Sponge.eventManager().unregisterListeners(listener);
    }
}
