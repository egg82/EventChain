package ninja.egg82.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

public class JDAEvents {
    private JDAEvents() {}

    public static <T extends GenericEvent> @NotNull JDAEventSubscriber<T> subscribe(@NotNull JDA jda, @NotNull Class<T> event) { return new JDAEventSubscriber<>(jda, event); }

    public static <E1 extends GenericEvent, T> @NotNull JDAMergedEventSubscriber<E1, T> merge(@NotNull JDA jda, @NotNull Class<T> superclass) { return new JDAMergedEventSubscriber<>(jda, superclass); }

    public static <E1 extends T, T extends GenericEvent> @NotNull JDAMergedEventSubscriber<E1, T> merge(@NotNull JDA jda, @NotNull Class<T> superclass, @NotNull Class<E1>... events) {
        JDAMergedEventSubscriber<E1, T> subscriber = new JDAMergedEventSubscriber<>(jda, superclass);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }
}
