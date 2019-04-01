package ninja.egg82.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

public class BukkitEvents {
    private BukkitEvents() {}

    public static <T extends Event> BukkitEventSubscriber<T> subscribe(Plugin plugin, Class<T> event, EventPriority priority) { return new BukkitEventSubscriber<>(plugin, event, priority); }

    public static void call(Event event) { Bukkit.getServer().getPluginManager().callEvent(event); }

    public static void callAsync(Plugin plugin, Event event) { Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> call(event)); }

    public static void callSync(Plugin plugin, Event event) { Bukkit.getScheduler().runTask(plugin, () -> call(event)); }

    public static <T> MergedBukkitEventSubscriber<T> merge(Plugin plugin, Class<T> commonClass) { return new MergedBukkitEventSubscriber<>(plugin, commonClass); }

    public static <T extends Event> MergedBukkitEventSubscriber<T> merge(Plugin plugin, Class<T> superclass, Class<? extends T>... events) { return merge(plugin, superclass, EventPriority.NORMAL, events); }

    public static <T extends Event> MergedBukkitEventSubscriber<T> merge(Plugin plugin, Class<T> superclass, EventPriority priority, Class<? extends T>... events) {
        MergedBukkitEventSubscriber<T> subscriber = new MergedBukkitEventSubscriber<>(plugin, superclass);
        for (Class<? extends T> clazz : events) {
            subscriber.bind(clazz, priority, e -> e);
        }
        return subscriber;
    }
}
