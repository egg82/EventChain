package ninja.egg82.events;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventPriority;
import ninja.egg82.events.internal.BungeeAllEventsListener;
import ninja.egg82.events.internal.BungeeHandlerMapping;

public class MergedBungeeEventSubscriber<T> extends MergedEventSubscriber<T> {
    private ConcurrentMap<Class<? extends Event>, BungeeHandlerMapping<T>> mappings = new ConcurrentHashMap<>();

    private Plugin plugin;
    private BungeeAllEventsListener listener;

    public MergedBungeeEventSubscriber(Plugin plugin, Class<T> commonClass) {
        super(commonClass);

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null.");
        }

        this.plugin = plugin;

        listener = new BungeeAllEventsListener(this);
        plugin.getProxy().getPluginManager().registerListener(plugin, listener);
    }

    public <E extends Event> MergedBungeeEventSubscriber<T> bind(Class<E> event, Function<E, T> function) { return bind(event, EventPriority.NORMAL, function); }

    public <E extends Event> MergedBungeeEventSubscriber<T> bind(Class<E> event, byte priority, Function<E, T> function) {
        if (event == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }
        if (function == null) {
            throw new IllegalArgumentException("function cannot be null.");
        }

        mappings.put(event, new BungeeHandlerMapping<>(priority, (Function<Event, T>) function));
        return this;
    }

    public Set<Class<? extends Event>> getEventClasses() { return mappings.keySet(); }

    public synchronized void call(T event) { throw new UnsupportedOperationException(); }

    public synchronized void call(Event event, byte priority) throws Exception {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }

        if (cancelled) {
            return;
        }

        BungeeHandlerMapping<T> mapping = mappings.get(event.getClass());
        if (mapping == null) {
            swallowException(null, new Exception("mapping does not exist for event class \"" + event.getClass() + "\"."));
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
        plugin.getProxy().getPluginManager().unregisterListener(listener);
    }

    public MergedBungeeEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (MergedBungeeEventSubscriber<T>) super.expireAfter(duration, unit); }

    public MergedBungeeEventSubscriber<T> expireAfterCalls(long calls) { return (MergedBungeeEventSubscriber<T>) super.expireAfterCalls(calls); }

    public MergedBungeeEventSubscriber<T> expireIf(Predicate<T> predicate) { return (MergedBungeeEventSubscriber<T>) super.expireIf(predicate); }

    public MergedBungeeEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (MergedBungeeEventSubscriber<T>) super.expireIf(predicate, stages); }

    public MergedBungeeEventSubscriber<T> expireIf(BiPredicate<MergedBungeeEventSubscriber<T>, T> predicate) { return (MergedBungeeEventSubscriber<T>) super.expireIfBi(predicate); }

    public MergedBungeeEventSubscriber<T> expireIf(BiPredicate<MergedBungeeEventSubscriber<T>, T> predicate, TestStage... stages) { return (MergedBungeeEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public MergedBungeeEventSubscriber<T> filter(Predicate<T> predicate) { return (MergedBungeeEventSubscriber<T>) super.filter(predicate); }

    public MergedBungeeEventSubscriber<T> filter(BiPredicate<MergedBungeeEventSubscriber<T>, T> predicate) { return (MergedBungeeEventSubscriber<T>) super.filterBi(predicate); }

    public MergedBungeeEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (MergedBungeeEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedBungeeEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (MergedBungeeEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedBungeeEventSubscriber<T> handler(Consumer<? super T> handler) { return (MergedBungeeEventSubscriber<T>) super.handler(handler); }

    public MergedBungeeEventSubscriber<T> handler(BiConsumer<MergedBungeeEventSubscriber<T>, ? super T> handler) { return (MergedBungeeEventSubscriber<T>) super.handlerBi(handler); }
}
