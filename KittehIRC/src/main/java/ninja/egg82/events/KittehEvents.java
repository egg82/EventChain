package ninja.egg82.events;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.helper.ClientEvent;

public class KittehEvents {
    private KittehEvents() { }

    public static <T extends ClientEvent> @NotNull KittehEventSubscriber<T> subscribe(@NotNull Client client, @NotNull Class<T> event) { return new KittehEventSubscriber<>(client, event); }

    public static <E1 extends ClientEvent, T> KittehMergedEventSubscriber<E1, T> merge(@NotNull Client client, @NotNull Class<T> superclass) { return new KittehMergedEventSubscriber<>(client, superclass); }

    public static <E1 extends T, T extends ClientEvent> @NotNull KittehMergedEventSubscriber<E1, T> merge(@NotNull Client client, @NotNull Class<T> superclass, @NotNull Class<E1>... events) {
        KittehMergedEventSubscriber<E1, T> subscriber = new KittehMergedEventSubscriber<>(client, superclass);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }
}
