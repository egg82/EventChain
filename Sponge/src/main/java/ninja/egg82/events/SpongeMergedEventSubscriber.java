package ninja.egg82.events;

import ninja.egg82.events.internal.SpongeHandlerMapping;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.Order;
import org.spongepowered.plugin.PluginContainer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class SpongeMergedEventSubscriber<E1 extends Event, T> extends AbstractMergedPriorityEventSubscriber<SpongeMergedEventSubscriber<E1, T>, Order, E1, T> {
    private final List<EventListener<E1>> listeners = new CopyOnWriteArrayList<>();

    private final PluginContainer plugin;
    private final boolean beforeModifications;

    public SpongeMergedEventSubscriber(@NotNull PluginContainer plugin, @NotNull Class<T> superclass, boolean beforeModifications) {
        super(superclass);

        this.plugin = plugin;
        this.beforeModifications = beforeModifications;
    }

    @Override
    public @NotNull SpongeMergedEventSubscriber<E1, T> bind(@NotNull Class<E1> event, @NotNull Order priority, @NotNull Function<E1, T> function) {
        mappings.put(event, new SpongeHandlerMapping<>(priority, function));

        EventListener<E1> listener = e -> {
            try {
                callMerged(e, priority);
            } catch (PriorityEventException ex) {
                throw new RuntimeException("Could not call merged event subscriber.", ex);
            }
        };
        listeners.add(listener);

        Sponge.eventManager().registerListener(EventListenerRegistration.builder(event).listener(listener).plugin(plugin).order(priority).beforeModifications(beforeModifications).build());
        return this;
    }

    @Override
    public void cancel() {
        super.cancel();

        for (EventListener<?> listener : listeners) {
            Sponge.eventManager().unregisterListeners(listener);
        }
        listeners.clear();
    }
}
