package ninja.egg82.events;

import net.kyori.event.EventBus;
import net.kyori.event.EventSubscriber;
import ninja.egg82.events.internal.AbstractKyoriSubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class KyoriEventSubscriber<T extends E, E> extends AbstractPriorityEventSubscriber<KyoriEventSubscriber<T, E>, Integer, T> {
    private final EventBus<E> bus;
    private final EventSubscriber<T> subscriber;

    public KyoriEventSubscriber(@NotNull EventBus<E> bus, @NotNull Class<T> event, int priority) {
        super(event);

        this.bus = bus;
        this.subscriber = new AbstractKyoriSubscriber<T>(event, priority) {
            @Override
            public void invoke(@NotNull T event) throws Exception {
                if (!baseClass.isInstance(event)) {
                    return;
                }

                try {
                    call(event, priority);
                } catch (PriorityEventException ex) {
                    throw new ExecutionException("Could not call event subscriber.", ex.getCause());
                }
            }
        };

        bus.register(event, subscriber);
    }

    @Override
    public void cancel() {
        super.cancel();
        bus.unregister(subscriber);
    }
}
