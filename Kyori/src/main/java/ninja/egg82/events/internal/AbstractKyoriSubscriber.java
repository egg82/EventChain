package ninja.egg82.events.internal;

import net.kyori.event.EventSubscriber;

public abstract class AbstractKyoriSubscriber<T> implements EventSubscriber<T> {
    private final int priority;

    protected AbstractKyoriSubscriber(int priority) {
        this.priority = priority;
    }

    @Override
    public int postOrder() { return priority; }

    @Override
    public boolean consumeCancelledEvents() { return true; }
}
