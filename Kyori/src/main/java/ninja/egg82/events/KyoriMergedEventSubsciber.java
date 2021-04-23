package ninja.egg82.events;

import net.kyori.event.EventBus;
import net.kyori.event.EventSubscriber;
import ninja.egg82.events.internal.AbstractKyoriSubscriber;
import ninja.egg82.events.internal.KyoriHandlerMapping;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class KyoriMergedEventSubsciber<E1, T> extends AbstractMergedPriorityEventSubscriber<KyoriMergedEventSubsciber<E1, T>, Integer, E1, T> {
    private final EventBus<E1> bus;
    private final List<EventSubscriber<E1>> subscribers = new CopyOnWriteArrayList<>();

    public KyoriMergedEventSubsciber(@NotNull EventBus<E1> bus, @NotNull Class<T> superclass) {
        super(superclass);

        this.bus = bus;
    }

    @Override
    @Deprecated
    public @NotNull KyoriMergedEventSubsciber<E1, T> bind(@NotNull Class<E1> event, @NotNull Integer priority, @NotNull Function<E1, T> function) {
        return bind(
                event,
                priority.intValue(),
                function
        );
    }

    public @NotNull KyoriMergedEventSubsciber<E1, T> bind(@NotNull Class<E1> event, int priority, @NotNull Function<E1, T> function) {
        mappings.put(event, new KyoriHandlerMapping<>(priority, function));

        EventSubscriber<E1> subscriber = new AbstractKyoriSubscriber<E1>(priority) {
            @Override
            public void invoke(@NotNull E1 event) throws Exception {
                try {
                    callMerged(event, priority);
                } catch (PriorityEventException ex) {
                    throw new ExecutionException("Could not call event subscriber.", ex);
                }
            }
        };
        subscribers.add(subscriber);

        bus.register(event, subscriber);

        return this;
    }

    @Override
    public void cancel() {
        super.cancel();

        for (EventSubscriber<?> subscriber : subscribers) {
            bus.unregister(subscriber);
        }
        subscribers.clear();
    }
}
