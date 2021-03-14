package ninja.egg82.events.internal;

import net.kyori.event.EventSubscriber;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Type;

public abstract class AbstractKyoriSubscriber<T> implements EventSubscriber<T> {
    private final Class<T> event;
    private final int priority;

    public AbstractKyoriSubscriber(Class<T> event, int priority) {
        this.event = event;
        this.priority = priority;
    }

    @Override
    public int postOrder() {
        return priority;
    }

    @Override
    public boolean consumeCancelledEvents() {
        return true;
    }

    @Override
    public @Nullable Type genericType() {
        return event;
    }
}
