package ninja.egg82.events;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown from an {@link EventSubscriber}.
 */
public class EventException extends Exception {
    private final EventSubscriber<?, ?> subscriber;
    private final Class<?> eventClass;
    private final SubscriberStage stage;

    public <S extends EventSubscriber<S, T>, T> EventException(@NotNull EventSubscriber<S, T> subscriber, @NotNull Class<T> eventClass, @NotNull SubscriberStage stage, String message) {
        super(message);
        this.subscriber = subscriber;
        this.eventClass = eventClass;
        this.stage = stage;
    }

    public <S extends EventSubscriber<S, T>, T> EventException(@NotNull EventSubscriber<S, T> subscriber, @NotNull Class<T> eventClass, @NotNull SubscriberStage stage, Throwable cause) {
        super(cause);
        this.subscriber = subscriber;
        this.eventClass = eventClass;
        this.stage = stage;
    }

    public <S extends EventSubscriber<S, T>, T> EventException(@NotNull EventSubscriber<S, T> subscriber, @NotNull Class<T> eventClass, @NotNull SubscriberStage stage, String message, Throwable cause) {
        super(message, cause);
        this.subscriber = subscriber;
        this.eventClass = eventClass;
        this.stage = stage;
    }

    /**
     * Gets the {@link EventSubscriber} this exception was thrown from.
     *
     * @return the subscriber this exception was thrown from.
     * @throws ClassCastException if the subscriber event type does not conform to the requested type
     */
    public @NotNull <S extends EventSubscriber<S, T>, T> S getSubscriber() { return (S) subscriber; }

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
