package ninja.egg82.events;

import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventPriority;

public class Events {
    private Events() {}

    public static <T extends Event> BungeeEventSubscriber<T> subscribe(Plugin plugin, Class<T> event, byte priority) { return new BungeeEventSubscriber<>(plugin, event, priority); }

    public static void call(Plugin plugin, Event event) { plugin.getProxy().getPluginManager().callEvent(event); }

    public static void callAsync(Plugin plugin, Event event) { plugin.getProxy().getScheduler().runAsync(plugin, () -> call(plugin, event)); }

    public static <T> MergedBungeeEventSubscriber<T> merge(Plugin plugin, Class<T> commonClass) { return new MergedBungeeEventSubscriber<>(plugin, commonClass); }

    public static <T extends Event> MergedBungeeEventSubscriber<T> merge(Plugin plugin, Class<T> superclass, Class<? extends T>... events) { return merge(plugin, superclass, EventPriority.NORMAL, events); }

    public static <T extends Event> MergedBungeeEventSubscriber<T> merge(Plugin plugin, Class<T> superclass, byte priority, Class<? extends T>... events) {
        MergedBungeeEventSubscriber<T> subscriber = new MergedBungeeEventSubscriber<>(plugin, superclass);
        for (Class<? extends T> clazz : events) {
            subscriber.bind(clazz, priority, e -> e);
        }
        return subscriber;
    }
}
