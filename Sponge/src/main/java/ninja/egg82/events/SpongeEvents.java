package ninja.egg82.events;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Order;

public class SpongeEvents {
    private SpongeEvents() { }

    public static <T extends Event> @NotNull SpongeEventSubscriber<T> subscribe(@NotNull Object plugin, @NotNull Class<T> event, @NotNull Order order, boolean beforeModifications) { return new SpongeEventSubscriber<>(plugin, event, order, beforeModifications); }

    public static void call(@NotNull Event event) { Sponge.getEventManager().post(event); }

    public static void callAsync(@NotNull Object plugin, @NotNull Event event) { Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> call(event)); }

    public static void callSync(@NotNull Object plugin, @NotNull Event event) { Sponge.getScheduler().createSyncExecutor(plugin).execute(() -> call(event)); }

    public static <E1 extends Event, T> SpongeMergedEventSubscriber<E1, T> merge(@NotNull Object plugin, @NotNull Class<T> superclass, boolean beforeModifications) { return new SpongeMergedEventSubscriber<>(plugin, superclass, beforeModifications); }

    public static <E1 extends T, T extends Event> @NotNull SpongeMergedEventSubscriber<E1, T> merge(@NotNull Object plugin, @NotNull Class<T> superclass, @NotNull Class<E1>... events) { return merge(plugin, superclass, Order.DEFAULT, false, events); }

    public static <E1 extends T, T extends Event> @NotNull SpongeMergedEventSubscriber<E1, T> merge(@NotNull Object plugin, @NotNull Class<T> superclass, @NotNull Order priority, boolean beforeModifications, @NotNull Class<E1>... events) {
        SpongeMergedEventSubscriber<E1, T> subscriber = new SpongeMergedEventSubscriber<>(plugin, superclass, beforeModifications);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, priority, e -> e);
        }
        return subscriber;
    }
}
