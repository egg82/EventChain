package ninja.egg82.events;

/**
 * Represents an event subscriber that supports
 * a single event of a particular class type.
 *
 * @param <S> the class type of the subscriber
 * @param <T> the class type of the event
 */
public interface SingleEventSubscriber<S extends SingleEventSubscriber<S, T>, T> extends EventSubscriber<S, T> { }
