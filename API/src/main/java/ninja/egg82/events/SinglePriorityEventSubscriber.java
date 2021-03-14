package ninja.egg82.events;

/**
 * Represents a priority-driven event subscriber that supports
 * a single event of a particular class type.
 *
 * @param <S> the class type of the subscriber
 * @param <P> the class type of the priority system
 * @param <T> the class type of the event
 */
public interface SinglePriorityEventSubscriber<S extends SinglePriorityEventSubscriber<S, P, T>, P, T> extends PriorityEventSubscriber<S, P, T> {
}
