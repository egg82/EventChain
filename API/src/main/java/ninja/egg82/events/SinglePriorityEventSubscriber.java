package ninja.egg82.events;

/**
 * Represents a priority-driven event subscriber that supports
 * a single event of a particular class type.
 *
 * @param <P> the class type of the priority system
 * @param <T> the class type of the event
 */
public interface SinglePriorityEventSubscriber<P, T> extends PriorityEventSubscriber<P, T> { }
