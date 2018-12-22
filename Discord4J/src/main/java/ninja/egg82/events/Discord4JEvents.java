package ninja.egg82.events;

import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventDispatcher;

public class Discord4JEvents {
    private Discord4JEvents() {}

    public static <T extends Event> Discord4JEventSubscriber<T> subscribe(EventDispatcher dispatcher, Class<T> event) { return new Discord4JEventSubscriber<>(dispatcher, event); }

    public static void call(EventDispatcher dispatcher, Event event) { dispatcher.dispatch(event); }

    public static <T> MergedDiscord4JEventSubscriber<T> merge(EventDispatcher dispatcher, Class<T> commonClass) { return new MergedDiscord4JEventSubscriber<>(dispatcher, commonClass); }

    public static <T extends Event> MergedDiscord4JEventSubscriber<T> merge(EventDispatcher dispatcher, Class<T> superclass, Class<? extends T>... events) {
        MergedDiscord4JEventSubscriber<T> subscriber = new MergedDiscord4JEventSubscriber<>(dispatcher, superclass);
        for (Class<? extends T> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }
}
