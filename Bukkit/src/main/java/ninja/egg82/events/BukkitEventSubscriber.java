package ninja.egg82.events;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class BukkitEventSubscriber<T extends Event> extends SingleEventSubscriber<T> implements Listener {
    private EventPriority priority;

    public BukkitEventSubscriber(Plugin plugin, Class<T> event, EventPriority priority) {
        super(event);

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null.");
        }
        if (priority == null) {
            throw new IllegalArgumentException("priority cannot be null.");
        }

        this.priority = priority;

        plugin.getServer().getPluginManager().registerEvent(event, this, priority, (l, e) -> {
            try {
                call((T) e, priority);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException("Could not call event handler.", ex);
            }
        }, plugin, false);
    }

    public synchronized void call(T event) { throw new UnsupportedOperationException(); }

    public synchronized void call(T event, EventPriority priority) throws Exception {
        if (priority == null) {
            throw new IllegalArgumentException("priority cannot be null.");
        }

        if (priority != this.priority) {
            return;
        }
        super.call(event);
    }

    public void cancel() {
        super.cancel();
        HandlerList.unregisterAll(this);
    }

    public BukkitEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (BukkitEventSubscriber<T>) super.expireAfter(duration, unit); }

    public BukkitEventSubscriber<T> expireAfterCalls(long calls) { return (BukkitEventSubscriber<T>) super.expireAfterCalls(calls); }

    public BukkitEventSubscriber<T> expireIf(Predicate<T> predicate) { return (BukkitEventSubscriber<T>) super.expireIf(predicate); }

    public BukkitEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (BukkitEventSubscriber<T>) super.expireIf(predicate, stages); }

    public BukkitEventSubscriber<T> expireIf(BiPredicate<BukkitEventSubscriber<T>, T> predicate) { return (BukkitEventSubscriber<T>) super.expireIfBi(predicate); }

    public BukkitEventSubscriber<T> expireIf(BiPredicate<BukkitEventSubscriber<T>, T> predicate, TestStage... stages) { return (BukkitEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public BukkitEventSubscriber<T> filter(Predicate<T> predicate) { return (BukkitEventSubscriber<T>) super.filter(predicate); }

    public BukkitEventSubscriber<T> filter(BiPredicate<BukkitEventSubscriber<T>, T> predicate) { return (BukkitEventSubscriber<T>) super.filterBi(predicate); }

    public BukkitEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (BukkitEventSubscriber<T>) super.exceptionHandler(consumer); }

    public BukkitEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (BukkitEventSubscriber<T>) super.exceptionHandler(consumer); }

    public BukkitEventSubscriber<T> handler(Consumer<? super T> handler) { return (BukkitEventSubscriber<T>) super.handler(handler); }

    public BukkitEventSubscriber<T> handler(BiConsumer<BukkitEventSubscriber<T>, ? super T> handler) { return (BukkitEventSubscriber<T>) super.handlerBi(handler); }
}
