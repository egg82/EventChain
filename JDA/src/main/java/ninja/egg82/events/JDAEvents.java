package ninja.egg82.events;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.Event;

public class JDAEvents {
    private JDAEvents() {}

    public static <T extends Event> JDAEventSubscriber<T> subscribe(JDA jda, Class<T> event) { return new JDAEventSubscriber<>(jda, event); }

    public static <T> MergedJDAEventSubscriber<T> merge(JDA jda, Class<T> commonClass) { return new MergedJDAEventSubscriber<>(jda, commonClass); }

    public static <T extends Event> MergedJDAEventSubscriber<T> merge(JDA jda, Class<T> superclass, Class<? extends T>... events) {
        MergedJDAEventSubscriber<T> subscriber = new MergedJDAEventSubscriber<>(jda, superclass);
        for (Class<? extends T> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }
}
