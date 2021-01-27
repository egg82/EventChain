package ninja.egg82.events;

/**
 * Represents an event subscriber that supports
 * a single event of a particular class type.
 * @param <T> the class type of the event
 */
public interface SingleEventSubscriber<T> extends EventSubscriber<T> { }
