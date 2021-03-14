package ninja.egg82.events;

import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventPriority;
import org.jetbrains.annotations.NotNull;

/**
 * The main way to create event subscribers.
 */
public class BungeeEvents {
    private BungeeEvents() { }

    /**
     * Returns a single event subscriber.
     *
     * @param <T> the class type of the event to listen to
     * @param plugin the plugin to listen to events with
     * @param event the event class to listen to
     *
     * @return a new {@link BungeeEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code plugin} or {@code event} is null
     */
    public static <T extends Event> @NotNull BungeeEventSubscriber<T> subscribe(@NotNull Plugin plugin, @NotNull Class<T> event) {
        return new BungeeEventSubscriber<>(
                plugin,
                event,
                EventPriority.NORMAL
        );
    }

    /**
     * Returns a single event subscriber.
     *
     * @param <T> the class type of the event to listen to
     * @param plugin the plugin to listen to events with
     * @param event the event class to listen to
     * @param priority the priority to listen on
     *
     * @return a new {@link BungeeEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code plugin}, {@code event}, or {@code priority} is null
     */
    public static <T extends Event> @NotNull BungeeEventSubscriber<T> subscribe(
            @NotNull Plugin plugin,
            @NotNull Class<T> event,
            byte priority
    ) { return new BungeeEventSubscriber<>(plugin, event, priority); }

    /**
     * Calls an event on the current thread.
     *
     * @param plugin the plugin to call the event with
     * @param event the event to call
     *
     * @throws NullPointerException if {@code event} is null
     */
    public static void call(@NotNull Plugin plugin, @NotNull Event event) {
        plugin.getProxy().getPluginManager().callEvent(event);
    }

    /**
     * Calls an event on a new async thread.
     *
     * @param plugin the plugin to call the event with
     * @param event the event to call
     *
     * @throws NullPointerException if {@code plugin} or {@code event} is null
     */
    public static void callAsync(@NotNull Plugin plugin, @NotNull Event event) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> call(plugin, event));
    }

    /**
     * Returns a merged event subscriber.
     *
     * @param <E1> the class type of the event to listen to
     * @param <T> the class type that will be processed in the handler
     * @param plugin the plugin to listen to events with
     * @param superclass the event class that will be processed in the handler
     *
     * @return a new {@link BungeeMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code plugin} or {@code superclass} is null
     */
    public static <E1 extends Event, T> @NotNull BungeeMergedEventSubscriber<E1, T> merge(
            @NotNull Plugin plugin,
            @NotNull Class<T> superclass
    ) { return new BungeeMergedEventSubscriber<>(plugin, superclass, EventPriority.NORMAL); }

    /**
     * Returns a merged event subscriber
     * that listens to multiple similar events.
     *
     * @param <E1> the class type of the event to listen to
     * @param <T> the class type that will be processed in the handler
     * @param plugin the plugin to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @param events the events to listen to
     *
     * @return a new {@link BungeeMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code plugin}, {@code superclass}, or {@code events} are null
     */
    public static <E1 extends T, T extends Event> @NotNull BungeeMergedEventSubscriber<E1, T> merge(
            @NotNull Plugin plugin, @NotNull Class<T> superclass, @NotNull Class<E1>... events
    ) { return merge(plugin, superclass, EventPriority.NORMAL, events); }

    /**
     * Returns a merged event subscriber
     * that listens to multiple similar events.
     *
     * @param <E1> the class type of the event to listen to
     * @param <T> the class type that will be processed in the handler
     * @param plugin the plugin to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @param priority the priority to listen on
     * @param events the events to listen to
     *
     * @return a new {@link BungeeMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code plugin}, {@code superclass}, {@code priority}, or {@code events} are null
     */
    public static <E1 extends T, T extends Event> @NotNull BungeeMergedEventSubscriber<E1, T> merge(
            @NotNull Plugin plugin, @NotNull Class<T> superclass, byte priority, @NotNull Class<E1>... events
    ) {
        BungeeMergedEventSubscriber<E1, T> subscriber = new BungeeMergedEventSubscriber<>(plugin, superclass, priority);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, priority, e -> e);
        }
        return subscriber;
    }
}
