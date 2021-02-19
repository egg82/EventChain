package ninja.egg82.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

public class JDAEvents {
    private JDAEvents() { }

    /**
     * Returns a single event subscriber.
     *
     * @param jda the JDA instance to listen to events with
     * @param event the event class to listen to
     * @return a new {@link JDAEventSubscriber} that listens to the desired event
     * @throws NullPointerException if {@code jda} or {@code event} is null
     */
    public static <T extends GenericEvent> @NotNull JDAEventSubscriber<T> subscribe(@NotNull JDA jda, @NotNull Class<T> event) { return new JDAEventSubscriber<>(jda, event); }

    /**
     * Returns a merged event subscriber.
     *
     * @param jda the JDA instance to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @return a new {@link JDAMergedEventSubscriber} that listens to the desired events
     * @throws NullPointerException if {@code jda} or {@code superclass} is null
     */
    public static <E1 extends GenericEvent, T> @NotNull JDAMergedEventSubscriber<E1, T> merge(@NotNull JDA jda, @NotNull Class<T> superclass) { return new JDAMergedEventSubscriber<>(jda, superclass); }

    /**
     * Returns a merged event subscriber
     * that listens to multiple similar events.
     *
     * @param jda the JDA instance to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @param events the events to listen to
     * @return a new {@link JDAMergedEventSubscriber} that listens to the desired events
     * @throws NullPointerException if {@code jda}, {@code superclass}, or {@code events} are null
     */
    public static <E1 extends T, T extends GenericEvent> @NotNull JDAMergedEventSubscriber<E1, T> merge(@NotNull JDA jda, @NotNull Class<T> superclass, @NotNull Class<E1>... events) {
        JDAMergedEventSubscriber<E1, T> subscriber = new JDAMergedEventSubscriber<>(jda, superclass);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }
}
