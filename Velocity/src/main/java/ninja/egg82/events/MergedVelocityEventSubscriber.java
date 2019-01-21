package ninja.egg82.events;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import ninja.egg82.events.internal.VelocityHandlerMapping;

public class MergedVelocityEventSubscriber<T> extends MergedEventSubscriber<T> {
    private ConcurrentMap<Class<?>, VelocityHandlerMapping<T>> mappings = new ConcurrentHashMap<>();
    private List<EventHandler<?>> handlers = new CopyOnWriteArrayList<>();

    private Object plugin;
    private ProxyServer proxy;

    public MergedVelocityEventSubscriber(Object plugin, ProxyServer proxy, Class<T> commonClass) {
        super(commonClass);

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null.");
        }
        if (proxy == null) {
            throw new IllegalArgumentException("proxy cannot be null.");
        }

        this.plugin = plugin;
        this.proxy = proxy;
    }

    public <E> MergedVelocityEventSubscriber<T> bind(Class<E> event, Function<E, T> function) { return bind(event, PostOrder.NORMAL, function); }

    public <E> MergedVelocityEventSubscriber<T> bind(Class<E> event, PostOrder order, Function<E, T> function) {
        if (event == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }
        if (function == null) {
            throw new IllegalArgumentException("function cannot be null.");
        }

        mappings.put(event, new VelocityHandlerMapping<>(order, (Function<Object, T>) function));

        EventHandler<E> handler = e -> {
            try {
                call(e, order);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException("Could not call event handler.", ex);
            }
        };
        handlers.add(handler);

        proxy.getEventManager().register(plugin, event, order, handler);

        return this;
    }

    public synchronized void call(T event) { throw new UnsupportedOperationException(); }

    public synchronized void call(Object event, PostOrder order) throws Exception {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }
        if (order == null) {
            throw new IllegalArgumentException("order cannot be null.");
        }

        if (cancelled) {
            return;
        }

        VelocityHandlerMapping<T> mapping = mappings.get(event.getClass());
        if (mapping == null) {
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

        for (EventHandler<?> handler : handlers) {
            proxy.getEventManager().unregister(plugin, handler);
        }
        handlers.clear();
    }

    public MergedVelocityEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (MergedVelocityEventSubscriber<T>) super.expireAfter(duration, unit); }

    public MergedVelocityEventSubscriber<T> expireAfterCalls(long calls) { return (MergedVelocityEventSubscriber<T>) super.expireAfterCalls(calls); }

    public MergedVelocityEventSubscriber<T> expireIf(Predicate<T> predicate) { return (MergedVelocityEventSubscriber<T>) super.expireIf(predicate); }

    public MergedVelocityEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (MergedVelocityEventSubscriber<T>) super.expireIf(predicate, stages); }

    public MergedVelocityEventSubscriber<T> expireIf(BiPredicate<MergedVelocityEventSubscriber<T>, T> predicate) { return (MergedVelocityEventSubscriber<T>) super.expireIfBi(predicate); }

    public MergedVelocityEventSubscriber<T> expireIf(BiPredicate<MergedVelocityEventSubscriber<T>, T> predicate, TestStage... stages) { return (MergedVelocityEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public MergedVelocityEventSubscriber<T> filter(Predicate<T> predicate) { return (MergedVelocityEventSubscriber<T>) super.filter(predicate); }

    public MergedVelocityEventSubscriber<T> filter(BiPredicate<MergedVelocityEventSubscriber<T>, T> predicate) { return (MergedVelocityEventSubscriber<T>) super.filterBi(predicate); }

    public MergedVelocityEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (MergedVelocityEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedVelocityEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (MergedVelocityEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedVelocityEventSubscriber<T> handler(Consumer<? super T> handler) { return (MergedVelocityEventSubscriber<T>) super.handler(handler); }

    public MergedVelocityEventSubscriber<T> handler(BiConsumer<MergedVelocityEventSubscriber<T>, ? super T> handler) { return (MergedVelocityEventSubscriber<T>) super.handlerBi(handler); }
}
