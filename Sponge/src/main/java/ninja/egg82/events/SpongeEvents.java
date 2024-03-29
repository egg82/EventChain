package ninja.egg82.events;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Order;
import org.spongepowered.plugin.PluginContainer;

public class SpongeEvents {
    private SpongeEvents() { }

    /**
     * Returns a single event subscriber.
     *
     * @param <T> the class type of the event to listen to
     * @param plugin the plugin to listen to events with
     * @param event the event class to listen to
     *
     * @return a new {@link SpongeEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code plugin} or {@code event} is null
     */
    public static <T extends Event> @NotNull SpongeEventSubscriber<T> subscribe(@NotNull PluginContainer plugin, @NotNull Class<T> event) {
        return new SpongeEventSubscriber<>(
                plugin,
                event,
                Order.DEFAULT,
                false
        );
    }

    /**
     * Returns a single event subscriber.
     *
     * @param <T> the class type of the event to listen to
     * @param plugin the plugin to listen to events with
     * @param event the event class to listen to
     * @param priority the priority to listen on
     * @param beforeModifications whether or not to call this subscriber before other server modifications
     *
     * @return a new {@link SpongeEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code plugin}, {@code event}, or {@code priority} is null
     */
    public static <T extends Event> @NotNull SpongeEventSubscriber<T> subscribe(
            @NotNull PluginContainer plugin, @NotNull Class<T> event, @NotNull Order priority, boolean beforeModifications
    ) { return new SpongeEventSubscriber<>(plugin, event, priority, beforeModifications); }

    /**
     * Calls an event on the current thread.
     *
     * @param event the event to call
     *
     * @throws NullPointerException if {@code event} is null
     */
    public static void call(@NotNull Event event) {
        Sponge.eventManager().post(event);
    }

    /**
     * Calls an event on a new async thread.
     *
     * @param plugin the plugin to call the event with
     * @param event the event to call
     *
     * @throws NullPointerException if {@code plugin} or {@code event} is null
     */
    public static void callAsync(@NotNull PluginContainer plugin, @NotNull Event event) {
        Sponge.asyncScheduler().executor(plugin).execute(() -> call(event));
    }

    /**
     * Calls an event on the next sync tick.
     *
     * @param plugin the plugin to call the event with
     * @param event the event to call
     *
     * @throws NullPointerException if {@code plugin} or {@code event} is null
     */
    public static void callSync(@NotNull PluginContainer plugin, @NotNull Event event) {
        Sponge.server().scheduler().executor(plugin).execute(() -> call(event)); // TODO: Is this actually sync?
    }

    /**
     * Returns a merged event subscriber.
     *
     * @param <E1> the class type of the event to listen to
     * @param <T> the class type that will be processed in the handler
     * @param plugin the plugin to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @param beforeModifications whether or not to call this subscriber before other server modifications
     *
     * @return a new {@link SpongeMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code plugin} or {@code superclass} is null
     */
    public static <E1 extends Event, T> SpongeMergedEventSubscriber<E1, T> merge(
            @NotNull PluginContainer plugin,
            @NotNull Class<T> superclass,
            boolean beforeModifications
    ) { return new SpongeMergedEventSubscriber<>(plugin, superclass, beforeModifications); }

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
     * @return a new {@link SpongeMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code plugin}, {@code superclass}, or {@code events} are null
     */
    @SafeVarargs
    public static <E1 extends T, T extends Event> @NotNull SpongeMergedEventSubscriber<E1, T> merge(
            @NotNull PluginContainer plugin, @NotNull Class<T> superclass, @NotNull Class<E1>... events
    ) { return merge(plugin, superclass, Order.DEFAULT, false, events); }

    /**
     * Returns a merged event subscriber
     * that listens to multiple similar events.
     *
     * @param <E1> the class type of the event to listen to
     * @param <T> the class type that will be processed in the handler
     * @param plugin the plugin to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @param priority the priority to listen on
     * @param beforeModifications whether or not to call this subscriber before other server modifications
     * @param events the events to listen to
     *
     * @return a new {@link SpongeMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code plugin}, {@code superclass}, {@code priority}, or {@code events} are null
     */
    @SafeVarargs
    public static <E1 extends T, T extends Event> @NotNull SpongeMergedEventSubscriber<E1, T> merge(
            @NotNull PluginContainer plugin, @NotNull Class<T> superclass, @NotNull Order priority, boolean beforeModifications, @NotNull Class<E1>... events
    ) {
        SpongeMergedEventSubscriber<E1, T> subscriber = new SpongeMergedEventSubscriber<>(plugin, superclass, beforeModifications);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, priority, e -> e);
        }
        return subscriber;
    }
}
