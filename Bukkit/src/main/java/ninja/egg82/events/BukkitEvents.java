package ninja.egg82.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BukkitEvents {
    private BukkitEvents() { }

    public static <T extends Event> @NotNull BukkitEventSubscriber<T> subscribe(@NotNull Plugin plugin, @NotNull Class<T> event) { return new BukkitEventSubscriber<>(plugin, event, EventPriority.NORMAL); }

    public static <T extends Event> @NotNull BukkitEventSubscriber<T> subscribe(@NotNull Plugin plugin, @NotNull Class<T> event, @NotNull EventPriority priority) { return new BukkitEventSubscriber<>(plugin, event, priority); }

    public static void call(@NotNull Event event) { Bukkit.getServer().getPluginManager().callEvent(event); }

    public static void callAsync(@NotNull Plugin plugin, @NotNull Event event) { Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> call(event)); }

    public static void callSync(@NotNull Plugin plugin, @NotNull Event event) { Bukkit.getScheduler().runTask(plugin, () -> call(event)); }

    public static <E1 extends Event, T> @NotNull BukkitMergedEventSubscriber<E1, T> merge(@NotNull Plugin plugin, @NotNull Class<T> superclass) { return new BukkitMergedEventSubscriber<>(plugin, superclass); }

    public static <E1 extends T, T extends Event> @NotNull BukkitMergedEventSubscriber<E1, T> merge(@NotNull Plugin plugin, @NotNull Class<T> superclass, @NotNull Class<E1>... events) { return merge(plugin, superclass, EventPriority.NORMAL, events); }

    public static <E1 extends T, T extends Event> @NotNull BukkitMergedEventSubscriber<E1, T> merge(@NotNull Plugin plugin, @NotNull Class<T> superclass, @NotNull EventPriority priority, @NotNull Class<E1>... events) {
        BukkitMergedEventSubscriber<E1, T> subscriber = new BukkitMergedEventSubscriber<>(plugin, superclass);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, priority, e -> e);
        }
        return subscriber;
    }
}
