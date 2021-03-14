package ninja.egg82.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * The main way to create event subscribers.
 */
public class BukkitEvents {
    private BukkitEvents() { }

    /**
     * Returns a single event subscriber.
     *
     * @param <T> the class type of the event to listen to
     * @param plugin the plugin to listen to events with
     * @param event the event class to listen to
     *
     * @return a new {@link BukkitEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code plugin} or {@code event} is null
     */
    public static <T extends Event> @NotNull BukkitEventSubscriber<T> subscribe(@NotNull Plugin plugin, @NotNull Class<T> event) {
        return new BukkitEventSubscriber<>(
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
     * @return a new {@link BukkitEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code plugin}, {@code event}, or {@code priority} is null
     */
    public static <T extends Event> @NotNull BukkitEventSubscriber<T> subscribe(
            @NotNull Plugin plugin,
            @NotNull Class<T> event,
            @NotNull EventPriority priority
    ) { return new BukkitEventSubscriber<>(plugin, event, priority); }

    /**
     * Calls an event on the current thread.
     *
     * @param event the event to call
     *
     * @throws NullPointerException if {@code event} is null
     */
    public static void call(@NotNull Event event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> call(event));
    }

    /**
     * Calls an event on the next sync tick.
     *
     * @param plugin the plugin to call the event with
     * @param event the event to call
     *
     * @throws NullPointerException if {@code plugin} or {@code event} is null
     */
    public static void callSync(@NotNull Plugin plugin, @NotNull Event event) {
        Bukkit.getScheduler().runTask(plugin, () -> call(event));
    }

    /**
     * Returns a merged event subscriber.
     *
     * @param <E1> the class type of the event to listen to
     * @param <T> the class type that will be processed in the handler
     * @param plugin the plugin to listen to events with
     * @param superclass the event class that will be processed in the handler
     *
     * @return a new {@link BukkitMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code plugin} or {@code superclass} is null
     */
    public static <E1 extends Event, T> @NotNull BukkitMergedEventSubscriber<E1, T> merge(
            @NotNull Plugin plugin,
            @NotNull Class<T> superclass
    ) { return new BukkitMergedEventSubscriber<>(plugin, superclass); }

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
     * @return a new {@link BukkitMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code plugin}, {@code superclass}, or {@code events} are null
     */
    @SafeVarargs
    public static <E1 extends T, T extends Event> @NotNull BukkitMergedEventSubscriber<E1, T> merge(
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
     * @return a new {@link BukkitMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code plugin}, {@code superclass}, {@code priority}, or {@code events} are null
     */
    @SafeVarargs
    public static <E1 extends T, T extends Event> @NotNull BukkitMergedEventSubscriber<E1, T> merge(
            @NotNull Plugin plugin, @NotNull Class<T> superclass, @NotNull EventPriority priority, @NotNull Class<E1>... events
    ) {
        BukkitMergedEventSubscriber<E1, T> subscriber = new BukkitMergedEventSubscriber<>(plugin, superclass);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, priority, e -> e);
        }
        return subscriber;
    }
}
