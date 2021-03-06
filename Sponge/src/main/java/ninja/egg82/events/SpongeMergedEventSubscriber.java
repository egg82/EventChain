package ninja.egg82.events;

import ninja.egg82.events.internal.SpongeHandlerMapping;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Order;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class SpongeMergedEventSubscriber<E1 extends Event, T> extends AbstractMergedPriorityEventSubscriber<SpongeMergedEventSubscriber<E1, T>, Order, E1, T> {
    private final List<EventListener<E1>> listeners = new CopyOnWriteArrayList<>();

    private final Object plugin;
    private final boolean beforeModifications;

    public SpongeMergedEventSubscriber(@NotNull Object plugin, @NotNull Class<T> superclass, boolean beforeModifications) {
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

        Sponge.getEventManager().registerListener(plugin, event, priority, beforeModifications, listener);
        return this;
    }

    @Override
    public void cancel() {
        super.cancel();

        for (EventListener<?> listener : listeners) {
            Sponge.getEventManager().unregisterListeners(listener);
        }
        listeners.clear();
    }
}
