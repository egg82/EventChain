package ninja.egg82.events;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import ninja.egg82.events.internal.BukkitHandlerMapping;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MergedBukkitEventSubscriber<T> extends MergedEventSubscriber<T> implements Listener {
    private JavaPlugin plugin;

    private ConcurrentMap<Class<? extends Event>, BukkitHandlerMapping<T>> mappings = new ConcurrentHashMap<>();

    public MergedBukkitEventSubscriber(JavaPlugin plugin, Class<T> commonClass) {
        super(commonClass);

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null.");
        }

        this.plugin = plugin;
    }

    public <E extends Event> MergedBukkitEventSubscriber<T> bind(Class<E> event, Function<E, T> function) { return bind(event, EventPriority.NORMAL, function); }

    public <E extends Event> MergedBukkitEventSubscriber<T> bind(Class<E> event, EventPriority priority, Function<E, T> function) {
        if (event == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }
        if (priority == null) {
            throw new IllegalArgumentException("priority cannot be null.");
        }
        if (function == null) {
            throw new IllegalArgumentException("function cannot be null.");
        }

        mappings.put(event, new BukkitHandlerMapping<>(priority, (Function<Event, T>) function));

        plugin.getServer().getPluginManager().registerEvent(event, this, priority, (l, e) -> {
            try {
                call(e, priority);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException("Could not call event handler.", ex);
            }
        }, plugin, false);

        return this;
    }

    public synchronized void call(T event) { throw new UnsupportedOperationException(); }

    public synchronized void call(Event event, EventPriority priority) throws Exception {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }
        if (priority == null) {
            throw new IllegalArgumentException("priority cannot be null.");
        }

        if (cancelled) {
            return;
        }

        BukkitHandlerMapping<T> mapping = mappings.get(event.getClass());
        if (mapping == null) {
            return;
        }

        if (mapping.getPriority() != priority) {
            return;
        }

        callCount++;

        if (expired) {
            return;
        }

        T obj = mapping.getFunction().apply(event);

        if (expire(obj, expirePredicates.get(TestStage.PRE_FILTER), expireBiPredicates.get(TestStage.PRE_FILTER))) {
            return;
        }

        if (filter(obj)) {
            return;
        }

        if (expire(obj, expirePredicates.get(TestStage.POST_FILTER), expireBiPredicates.get(TestStage.POST_FILTER))) {
            return;
        }

        for (Consumer<? super T> consumer : handlerConsumers) {
            try {
                consumer.accept(obj);
            } catch (Exception ex) {
                swallowException(obj, ex);
            }
        }
        for (BiConsumer<? extends MergedEventSubscriber<T>, ? super T> consumer : handlerBiConsumers) {
            BiConsumer<MergedEventSubscriber<T>, ? super T> c = (BiConsumer<MergedEventSubscriber<T>, ? super T>) consumer;
            try {
                c.accept(this, obj);
            } catch (Exception ex) {
                swallowException(obj, ex);
            }
        }

        expire(obj, expirePredicates.get(TestStage.POST_HANDLE), expireBiPredicates.get(TestStage.POST_HANDLE));
    }

    public void cancel() {
        super.cancel();
        HandlerList.unregisterAll(this);
    }

    public MergedBukkitEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (MergedBukkitEventSubscriber<T>) super.expireAfter(duration, unit); }

    public MergedBukkitEventSubscriber<T> expireAfterCalls(long calls) { return (MergedBukkitEventSubscriber<T>) super.expireAfterCalls(calls); }

    public MergedBukkitEventSubscriber<T> expireIf(Predicate<T> predicate) { return (MergedBukkitEventSubscriber<T>) super.expireIf(predicate); }

    public MergedBukkitEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (MergedBukkitEventSubscriber<T>) super.expireIf(predicate, stages); }

    public MergedBukkitEventSubscriber<T> expireIf(BiPredicate<MergedBukkitEventSubscriber<T>, T> predicate) { return (MergedBukkitEventSubscriber<T>) super.expireIfBi(predicate); }

    public MergedBukkitEventSubscriber<T> expireIf(BiPredicate<MergedBukkitEventSubscriber<T>, T> predicate, TestStage... stages) { return (MergedBukkitEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public MergedBukkitEventSubscriber<T> filter(Predicate<T> predicate) { return (MergedBukkitEventSubscriber<T>) super.filter(predicate); }

    public MergedBukkitEventSubscriber<T> filter(BiPredicate<MergedBukkitEventSubscriber<T>, T> predicate) { return (MergedBukkitEventSubscriber<T>) super.filterBi(predicate); }

    public MergedBukkitEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (MergedBukkitEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedBukkitEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (MergedBukkitEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedBukkitEventSubscriber<T> handler(Consumer<? super T> handler) { return (MergedBukkitEventSubscriber<T>) super.handler(handler); }

    public MergedBukkitEventSubscriber<T> handler(BiConsumer<MergedBukkitEventSubscriber<T>, ? super T> handler) { return (MergedBukkitEventSubscriber<T>) super.handlerBi(handler); }
}
