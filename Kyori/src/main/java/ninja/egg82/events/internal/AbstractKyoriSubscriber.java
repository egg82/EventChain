package ninja.egg82.events.internal;

import java.lang.reflect.Type;
import net.kyori.event.EventSubscriber;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractKyoriSubscriber<T> implements EventSubscriber<T> {
    private final Class<T> event;
    private final int priority;

    public AbstractKyoriSubscriber(Class<T> event, int priority) {
        this.event = event;
        this.priority = priority;
    }

    public int postOrder() { return priority; }

    public boolean consumeCancelledEvents() { return true; }

    public @Nullable Type genericType() { return event; }
}
