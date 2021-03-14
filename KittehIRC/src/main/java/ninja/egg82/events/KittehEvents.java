package ninja.egg82.events;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.helper.ClientEvent;

public class KittehEvents {
    private KittehEvents() { }

    /**
     * Returns a single event subscriber.
     *
     * @param <T> the class type of the event to listen to
     * @param client the client instance to listen to events with
     * @param event the event class to listen to
     *
     * @return a new {@link KittehEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code client} or {@code event} is null
     */
    public static <T extends ClientEvent> @NotNull KittehEventSubscriber<T> subscribe(
            @NotNull Client client,
            @NotNull Class<T> event
    ) { return new KittehEventSubscriber<>(client, event); }

    /**
     * Returns a merged event subscriber.
     *
     * @param <E1> the class type of the event to listen to
     * @param <T> the class type that will be processed in the handler
     * @param client the client instance to listen to events with
     * @param superclass the event class that will be processed in the handler
     *
     * @return a new {@link KittehMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code client} or {@code superclass} is null
     */
    public static <E1 extends ClientEvent, T> KittehMergedEventSubscriber<E1, T> merge(
            @NotNull Client client,
            @NotNull Class<T> superclass
    ) { return new KittehMergedEventSubscriber<>(client, superclass); }

    /**
     * Returns a merged event subscriber
     * that listens to multiple similar events.
     *
     * @param <E1> the class type of the event to listen to
     * @param <T> the class type that will be processed in the handler
     * @param client the client instance to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @param events the events to listen to
     *
     * @return a new {@link KittehMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code client}, {@code superclass}, or {@code events} are null
     */
    public static <E1 extends T, T extends ClientEvent> @NotNull KittehMergedEventSubscriber<E1, T> merge(
            @NotNull Client client, @NotNull Class<T> superclass, @NotNull Class<E1>... events
    ) {
        KittehMergedEventSubscriber<E1, T> subscriber = new KittehMergedEventSubscriber<>(client, superclass);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }
}
