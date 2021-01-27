package ninja.egg82.events;

import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventPriority;
import org.jetbrains.annotations.NotNull;

public class BungeeEvents {
    private BungeeEvents() {}

    public static <T extends Event> @NotNull BungeeEventSubscriber<T> subscribe(@NotNull Plugin plugin, @NotNull Class<T> event) { return new BungeeEventSubscriber<>(plugin, event, EventPriority.NORMAL); }

    public static <T extends Event> @NotNull BungeeEventSubscriber<T> subscribe(@NotNull Plugin plugin, @NotNull Class<T> event, byte priority) { return new BungeeEventSubscriber<>(plugin, event, priority); }

    public static void call(@NotNull Plugin plugin, @NotNull Event event) { plugin.getProxy().getPluginManager().callEvent(event); }

    public static void callAsync(@NotNull Plugin plugin, @NotNull Event event) { plugin.getProxy().getScheduler().runAsync(plugin, () -> call(plugin, event)); }

    public static <E1 extends Event, T> @NotNull BungeeMergedEventSubscriber<E1, T> merge(@NotNull Plugin plugin, @NotNull Class<T> superclass) { return new BungeeMergedEventSubscriber<>(plugin, superclass, EventPriority.NORMAL); }

    public static <E1 extends T, T extends Event> @NotNull BungeeMergedEventSubscriber<E1, T> merge(@NotNull Plugin plugin, @NotNull Class<T> superclass, @NotNull Class<E1>... events) { return merge(plugin, superclass, EventPriority.NORMAL, events); }

    public static <E1 extends T, T extends Event> @NotNull BungeeMergedEventSubscriber<E1, T> merge(@NotNull Plugin plugin, @NotNull Class<T> superclass, byte priority, @NotNull Class<E1>... events) {
        BungeeMergedEventSubscriber<E1, T> subscriber = new BungeeMergedEventSubscriber<>(plugin, superclass, priority);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, priority, e -> e);
        }
        return subscriber;
    }
}
