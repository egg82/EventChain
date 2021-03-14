package ninja.egg82.events;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;

public class VelocityEvents {
    private VelocityEvents() { }

    /**
     * Returns a single event subscriber.
     *
     * @param <T> the class type of the event to listen to
     * @param plugin the plugin to listen to events with
     * @param proxy the proxy to listen to events with
     * @param event the event class to listen to
     *
     * @return a new {@link VelocityEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code plugin}, {@code proxy}, or {@code event} is null
     */
    public static <T> @NotNull VelocityEventSubscriber<T> subscribe(
            @NotNull Object plugin,
            @NotNull ProxyServer proxy,
            @NotNull Class<T> event
    ) { return new VelocityEventSubscriber<>(plugin, proxy, event, PostOrder.NORMAL); }

    /**
     * Returns a single event subscriber.
     *
     * @param <T> the class type of the event to listen to
     * @param plugin the plugin to listen to events with
     * @param proxy the proxy to listen to events with
     * @param event the event class to listen to
     * @param priority the priority to listen on
     *
     * @return a new {@link VelocityEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code plugin}, {@code proxy} {@code event}, or {@code priority} is null
     */
    public static <T> @NotNull VelocityEventSubscriber<T> subscribe(
            @NotNull Object plugin, @NotNull ProxyServer proxy, @NotNull Class<T> event, @NotNull PostOrder priority
    ) { return new VelocityEventSubscriber<>(plugin, proxy, event, priority); }

    /**
     * Calls an event on the current thread.
     *
     * @param proxy the proxy to call the event with
     * @param event the event to call
     *
     * @throws NullPointerException if {@code proxy} or {@code event} is null
     */
    public static void call(@NotNull ProxyServer proxy, @NotNull Object event) {
        proxy.getEventManager().fireAndForget(event);
    }

    /**
     * Calls an event on a new async thread.
     *
     * @param plugin the plugin to call the event with
     * @param proxy the proxy to call the event with
     * @param event the event to call
     *
     * @throws NullPointerException if {@code plugin}, {@code proxy}, or {@code event} is null
     */
    public static void callAsync(@NotNull Object plugin, @NotNull ProxyServer proxy, @NotNull Object event) {
        proxy.getScheduler().buildTask(plugin, () -> call(proxy, event));
    }

    /**
     * Returns a merged event subscriber.
     *
     * @param <E1> the class type of the event to listen to
     * @param <T> the class type that will be processed in the handler
     * @param plugin the plugin to listen to events with
     * @param proxy the proxy to listen to events with
     * @param superclass the event class that will be processed in the handler
     *
     * @return a new {@link VelocityMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code plugin}, {@code proxy}, or {@code superclass} is null
     */
    public static <E1, T> @NotNull VelocityMergedEventSubscriber<E1, T> merge(
            @NotNull Object plugin,
            @NotNull ProxyServer proxy,
            @NotNull Class<T> superclass
    ) { return new VelocityMergedEventSubscriber<>(plugin, proxy, superclass); }

    /**
     * Returns a merged event subscriber
     * that listens to multiple similar events.
     *
     * @param <E1> the class type of the event to listen to
     * @param <T> the class type that will be processed in the handler
     * @param plugin the plugin to listen to events with
     * @param proxy the proxy to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @param events the events to listen to
     *
     * @return a new {@link VelocityMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code plugin}, {@code proxy}, {@code superclass}, or {@code events} are null
     */
    @SafeVarargs
    public static <E1 extends T, T> @NotNull VelocityMergedEventSubscriber<E1, T> merge(
            @NotNull Object plugin, @NotNull ProxyServer proxy, @NotNull Class<T> superclass, @NotNull Class<E1>... events
    ) { return merge(plugin, proxy, superclass, PostOrder.NORMAL, events); }

    /**
     * Returns a merged event subscriber
     * that listens to multiple similar events.
     *
     * @param <E1> the class type of the event to listen to
     * @param <T> the class type that will be processed in the handler
     * @param plugin the plugin to listen to events with
     * @param proxy the proxy to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @param priority the priority to listen on
     * @param events the events to listen to
     *
     * @return a new {@link VelocityMergedEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code plugin}, {@code proxy}, {@code superclass}, {@code priority}, or {@code events} are null
     */
    @SafeVarargs
    public static <E1 extends T, T> @NotNull VelocityMergedEventSubscriber<E1, T> merge(
            @NotNull Object plugin, @NotNull ProxyServer proxy, @NotNull Class<T> superclass, @NotNull PostOrder priority, @NotNull Class<E1>... events
    ) {
        VelocityMergedEventSubscriber<E1, T> subscriber = new VelocityMergedEventSubscriber<>(plugin, proxy, superclass);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, priority, e -> e);
        }
        return subscriber;
    }
}
