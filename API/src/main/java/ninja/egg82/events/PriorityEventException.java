package ninja.egg82.events;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown from an {@link PriorityEventSubscriber}.
 */
public class PriorityEventException extends Exception {
    private final transient PriorityEventSubscriber<?, ?, ?> subscriber;
    private final Class<?> eventClass;
    private final SubscriberStage stage;

    public <S extends PriorityEventSubscriber<S, P, T>, P, T> PriorityEventException(
            @NotNull PriorityEventSubscriber<S, P, T> subscriber, @NotNull Class<T> eventClass, @NotNull SubscriberStage stage, String message
    ) {
        super(message);
        this.subscriber = subscriber;
        this.eventClass = eventClass;
        this.stage = stage;
    }

    public <S extends PriorityEventSubscriber<S, P, T>, P, T> PriorityEventException(
            @NotNull PriorityEventSubscriber<S, P, T> subscriber, @NotNull Class<T> eventClass, @NotNull SubscriberStage stage, Throwable cause
    ) {
        super(cause);
        this.subscriber = subscriber;
        this.eventClass = eventClass;
        this.stage = stage;
    }

    public <S extends PriorityEventSubscriber<S, P, T>, P, T> PriorityEventException(
            @NotNull PriorityEventSubscriber<S, P, T> subscriber, @NotNull Class<T> eventClass, @NotNull SubscriberStage stage, String message, Throwable cause
    ) {
        super(message, cause);
        this.subscriber = subscriber;
        this.eventClass = eventClass;
        this.stage = stage;
    }

    /**
     * Gets the {@link PriorityEventSubscriber} this exception was thrown from.
     *
     * @return the subscriber this exception was thrown from.
     *
     * @throws ClassCastException if the subscriber event type does not conform to the requested type
     */
    @SuppressWarnings("unchecked")
    public @NotNull <S extends PriorityEventSubscriber<S, P, T>, P, T> S getSubscriber() { return (S) subscriber; }

    /**
     * Gets the subscriber's event class from the subscriber this exception was thrown from.
     *
     * @return the subscriber's event class
     */
    public @NotNull Class<?> getEventClass() { return eventClass; }

    /**
     * Gets the {@link SubscriberStage} that this exception was thrown from.
     *
     * @return the subscriber's stage
     */
    public @NotNull SubscriberStage getStage() { return stage; }
}
