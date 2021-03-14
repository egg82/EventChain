package ninja.egg82.events;

import net.kyori.event.EventBus;
import net.kyori.event.PostOrders;
import org.jetbrains.annotations.NotNull;

public class KyoriEvents {
    private KyoriEvents() { }

    /**
     * Returns a single event subscriber.
     *
     * @param <T> the class type of the event to listen to
     * @param <E> the class type of the event bus
     * @param bus the bus to listen to events with
     * @param event the event class to listen to
     *
     * @return a new {@link KyoriEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code bus} or {@code event} is null
     */
    public static <T extends E, E> @NotNull KyoriEventSubscriber<T, E> subscribe(@NotNull EventBus<E> bus, @NotNull Class<T> event) {
        return new KyoriEventSubscriber<>(
                bus,
                event,
                PostOrders.NORMAL
        );
    }

    /**
     * Returns a single event subscriber.
     *
     * @param <T> the class type of the event to listen to
     * @param <E> the class type of the event bus
     * @param bus the bus to listen to events with
     * @param event the event class to listen to
     * @param priority the priority to listen on
     *
     * @return a new {@link KyoriEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code bus} or {@code event} is null
     */
    public static <T extends E, E> @NotNull KyoriEventSubscriber<T, E> subscribe(
            @NotNull EventBus<E> bus,
            @NotNull Class<T> event,
            int priority
    ) { return new KyoriEventSubscriber<>(bus, event, priority); }

    /**
     * Calls an event on the current thread.
     *
     * @param <T> the class type of the event to call
     * @param <E> the class type of the event bus
     * @param bus the bus to call the event with
     * @param event the event to call
     *
     * @throws NullPointerException if {@code proxy} or {@code event} is null
     */
    public static <T extends E, E> void call(@NotNull EventBus<E> bus, @NotNull T event) {
        bus.post(event);
    }

    /**
     * Returns a merged event subscriber.
     *
     * @param <E1> the class type of the event bus
     * @param <T> the class type that will be processed in the handler
     * @param bus the bus to listen to events with
     * @param superclass the event class that will be processed in the handler
     *
     * @return a new {@link KyoriMergedEventSubsciber} that listens to the desired events
     *
     * @throws NullPointerException if {@code bus} or {@code superclass} is null
     */
    public static <E1, T> @NotNull KyoriMergedEventSubsciber<E1, T> merge(
            @NotNull EventBus<E1> bus,
            @NotNull Class<T> superclass
    ) { return new KyoriMergedEventSubsciber<>(bus, superclass); }

    /**
     * Returns a merged event subscriber
     * that listens to multiple similar events.
     *
     * @param <E1> the class type of the event bus
     * @param <T> the class type that will be processed in the handler
     * @param bus the bus to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @param events the events to listen to
     *
     * @return a new {@link KyoriMergedEventSubsciber} that listens to the desired events
     *
     * @throws NullPointerException if {@code bus}, {@code superclass}, or {@code events} are null
     */
    @SafeVarargs
    public static <E1 extends T, T> @NotNull KyoriMergedEventSubsciber<E1, T> merge(
            @NotNull EventBus<E1> bus, @NotNull Class<T> superclass, @NotNull Class<E1>... events
    ) { return merge(bus, superclass, PostOrders.NORMAL, events); }

    /**
     * Returns a merged event subscriber
     * that listens to multiple similar events.
     *
     * @param <E1> the class type of the event bus
     * @param <T> the class type that will be processed in the handler
     * @param bus the bus to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @param priority the priority to listen on
     * @param events the events to listen to
     *
     * @return a new {@link KyoriMergedEventSubsciber} that listens to the desired events
     *
     * @throws NullPointerException if {@code bus}, {@code superclass}, or {@code events} are null
     */
    @SafeVarargs
    public static <E1 extends T, T> @NotNull KyoriMergedEventSubsciber<E1, T> merge(
            @NotNull EventBus<E1> bus, @NotNull Class<T> superclass, int priority, @NotNull Class<E1>... events
    ) {
        KyoriMergedEventSubsciber<E1, T> subscriber = new KyoriMergedEventSubsciber<>(bus, superclass);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, priority, e -> e);
        }
        return subscriber;
    }
}
