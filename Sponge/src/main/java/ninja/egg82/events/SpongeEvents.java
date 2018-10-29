package ninja.egg82.events;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Order;

public class SpongeEvents {
    private SpongeEvents() {}

    public static <T extends Event> SpongeEventSubscriber<T> subscribe(Object plugin, Class<T> event, Order order, boolean beforeModifications) { return new SpongeEventSubscriber<>(plugin, event, order, beforeModifications); }

    public static void call(Event event) { Sponge.getEventManager().post(event); }

    public static void callAsync(Object plugin, Event event) { Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> call(event)); }

    public static void callSync(Object plugin, Event event) { Sponge.getScheduler().createSyncExecutor(plugin).execute(() -> call(event)); }

    public static <T> MergedSpongeEventSubscriber<T> merge(Object plugin, Class<T> commonClass) { return new MergedSpongeEventSubscriber<>(plugin, commonClass); }

    public static <T extends Event> MergedSpongeEventSubscriber<T> merge(Object plugin, Class<T> superclass, Class<? extends T>... events) { return merge(plugin, superclass, Order.DEFAULT, false, events); }

    public static <T extends Event> MergedSpongeEventSubscriber<T> merge(Object plugin, Class<T> superclass, Order order, boolean beforeModifications, Class<? extends T>... events) {
        MergedSpongeEventSubscriber<T> subscriber = new MergedSpongeEventSubscriber<>(plugin, superclass);
        for (Class<? extends T> clazz : events) {
            subscriber.bind(clazz, order, beforeModifications, e -> e);
        }
        return subscriber;
    }
}
