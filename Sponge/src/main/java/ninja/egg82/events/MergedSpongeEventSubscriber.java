package ninja.egg82.events;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import ninja.egg82.events.internal.SpongeHandlerMapping;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Order;

public class MergedSpongeEventSubscriber<T> extends MergedEventSubscriber<T> {
    private Object plugin;

    private ConcurrentMap<Class<? extends Event>, SpongeHandlerMapping<T>> mappings = new ConcurrentHashMap<>();
    private List<EventListener<?>> listeners = new CopyOnWriteArrayList<>();

    public MergedSpongeEventSubscriber(Object plugin, Class<T> commonClass) {
        super(commonClass);

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null.");
        }

        this.plugin = plugin;
    }

    public <E extends Event> MergedSpongeEventSubscriber<T> bind(Class<E> event, boolean beforeModifications, Function<E, T> function) { return bind(event, Order.DEFAULT, beforeModifications, function); }

    public <E extends Event> MergedSpongeEventSubscriber<T> bind(Class<E> event, Order order, boolean beforeModifications, Function<E, T> function) {
        if (event == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }
        if (order == null) {
            throw new IllegalArgumentException("order cannot be null.");
        }
        if (function == null) {
            throw new IllegalArgumentException("function cannot be null.");
        }

        mappings.put(event, new SpongeHandlerMapping<>(order, (Function<Event, T>) function));

        EventListener<E> listener = e -> {
            try {
                call(e, order);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException("Could not call event handler.", ex);
            }
        };
        listeners.add(listener);

        Sponge.getEventManager().registerListener(plugin, event, order, beforeModifications, listener);

        return this;
    }

    public synchronized void call(T event) { throw new UnsupportedOperationException(); }

    public synchronized void call(Event event, Order order) throws Exception {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }
        if (order == null) {
            throw new IllegalArgumentException("order cannot be null.");
        }

        if (cancelled) {
            return;
        }

        SpongeHandlerMapping<T> mapping = mappings.get(event.getClass());
        if (mapping == null) {
            swallowException(null, new Exception("mapping does not exist for event class \"" + event.getClass() + "\"."));
            return;
        }

        if (mapping.getOrder() != order) {
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
            } catch (ClassCastException ignored) {

            } catch (Exception ex) {
                swallowException(obj, ex);
            }
        }
        for (BiConsumer<? extends MergedEventSubscriber<T>, ? super T> consumer : handlerBiConsumers) {
            BiConsumer<MergedEventSubscriber<T>, ? super T> c = (BiConsumer<MergedEventSubscriber<T>, ? super T>) consumer;
            try {
                c.accept(this, obj);
            } catch (ClassCastException ignored) {

            } catch (Exception ex) {
                swallowException(obj, ex);
            }
        }

        expire(obj, expirePredicates.get(TestStage.POST_HANDLE), expireBiPredicates.get(TestStage.POST_HANDLE));
    }

    public void cancel() {
        super.cancel();

        for (EventListener<?> listener : listeners) {
            Sponge.getEventManager().unregisterListeners(listener);
        }
        listeners.clear();
    }

    public MergedSpongeEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (MergedSpongeEventSubscriber<T>) super.expireAfter(duration, unit); }

    public MergedSpongeEventSubscriber<T> expireAfterCalls(long calls) { return (MergedSpongeEventSubscriber<T>) super.expireAfterCalls(calls); }

    public MergedSpongeEventSubscriber<T> expireIf(Predicate<T> predicate) { return (MergedSpongeEventSubscriber<T>) super.expireIf(predicate); }

    public MergedSpongeEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (MergedSpongeEventSubscriber<T>) super.expireIf(predicate, stages); }

    public MergedSpongeEventSubscriber<T> expireIf(BiPredicate<MergedSpongeEventSubscriber<T>, T> predicate) { return (MergedSpongeEventSubscriber<T>) super.expireIfBi(predicate); }

    public MergedSpongeEventSubscriber<T> expireIf(BiPredicate<MergedSpongeEventSubscriber<T>, T> predicate, TestStage... stages) { return (MergedSpongeEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public MergedSpongeEventSubscriber<T> filter(Predicate<T> predicate) { return (MergedSpongeEventSubscriber<T>) super.filter(predicate); }

    public MergedSpongeEventSubscriber<T> filter(BiPredicate<MergedSpongeEventSubscriber<T>, T> predicate) { return (MergedSpongeEventSubscriber<T>) super.filterBi(predicate); }

    public MergedSpongeEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (MergedSpongeEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedSpongeEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (MergedSpongeEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedSpongeEventSubscriber<T> handler(Consumer<? super T> handler) { return (MergedSpongeEventSubscriber<T>) super.handler(handler); }

    public MergedSpongeEventSubscriber<T> handler(BiConsumer<MergedSpongeEventSubscriber<T>, ? super T> handler) { return (MergedSpongeEventSubscriber<T>) super.handlerBi(handler); }
}
