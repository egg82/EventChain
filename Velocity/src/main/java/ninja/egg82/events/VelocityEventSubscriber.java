package ninja.egg82.events;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class VelocityEventSubscriber<T> extends AbstractSingleEventSubscriber<T> {
    private PostOrder order;

    private Object plugin;
    private ProxyServer proxy;

    private EventHandler<T> handler;

    public VelocityEventSubscriber(Object plugin, ProxyServer proxy, Class<T> event, PostOrder order) {
        super(event);

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null.");
        }
        if (proxy == null) {
            throw new IllegalArgumentException("proxy cannot be null.");
        }

        this.order = order;
        this.plugin = plugin;
        this.proxy = proxy;

        handler = e -> {
            try {
                call(e, order);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException("Could not call event handler.", ex);
            }
        };

        proxy.getEventManager().register(plugin, event, order, handler);
    }

    public synchronized void call(T event) { throw new UnsupportedOperationException(); }

    public synchronized void call(T event, PostOrder order) throws Exception {
        if (order == null) {
            throw new IllegalArgumentException("order cannot be null.");
        }

        if (order != this.order) {
            return;
        }
        super.call(event);
    }

    public Class<T> getEventClass() { return super.getEventClass(); }

    public void cancel() {
        super.cancel();
        proxy.getEventManager().unregister(plugin, handler);
    }

    public VelocityEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (VelocityEventSubscriber<T>) super.expireAfter(duration, unit); }

    public VelocityEventSubscriber<T> expireAfterCalls(long calls) { return (VelocityEventSubscriber<T>) super.expireAfterCalls(calls); }

    public VelocityEventSubscriber<T> expireIf(Predicate<T> predicate) { return (VelocityEventSubscriber<T>) super.expireIf(predicate); }

    public VelocityEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (VelocityEventSubscriber<T>) super.expireIf(predicate, stages); }

    public VelocityEventSubscriber<T> expireIf(BiPredicate<VelocityEventSubscriber<T>, T> predicate) { return (VelocityEventSubscriber<T>) super.expireIfBi(predicate); }

    public VelocityEventSubscriber<T> expireIf(BiPredicate<VelocityEventSubscriber<T>, T> predicate, TestStage... stages) { return (VelocityEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public VelocityEventSubscriber<T> filter(Predicate<T> predicate) { return (VelocityEventSubscriber<T>) super.filter(predicate); }

    public VelocityEventSubscriber<T> filter(BiPredicate<VelocityEventSubscriber<T>, T> predicate) { return (VelocityEventSubscriber<T>) super.filterBi(predicate); }

    public VelocityEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (VelocityEventSubscriber<T>) super.exceptionHandler(consumer); }

    public VelocityEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (VelocityEventSubscriber<T>) super.exceptionHandler(consumer); }

    public VelocityEventSubscriber<T> handler(Consumer<? super T> handler) { return (VelocityEventSubscriber<T>) super.handler(handler); }

    public VelocityEventSubscriber<T> handler(BiConsumer<VelocityEventSubscriber<T>, ? super T> handler) { return (VelocityEventSubscriber<T>) super.handlerBi(handler); }
}
