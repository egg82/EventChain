package ninja.egg82.events;

import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.helper.ClientEvent;

public class KittehEvents {
    private KittehEvents() {}

    public static <T extends ClientEvent> KittehEventSubscriber<T> subscribe(Client client, Class<T> event) { return new KittehEventSubscriber<>(client, event); }

    public static <T> MergedKittehEventSubscriber<T> merge(Client client, Class<T> commonClass) { return new MergedKittehEventSubscriber<>(client, commonClass); }

    public static <T extends ClientEvent> MergedKittehEventSubscriber<T> merge(Client client, Class<T> superclass, Class<? extends T>... events) {
        MergedKittehEventSubscriber<T> subscriber = new MergedKittehEventSubscriber<>(client, superclass);
        for (Class<? extends T> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }
}
