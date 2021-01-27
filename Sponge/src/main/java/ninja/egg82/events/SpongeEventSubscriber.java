package ninja.egg82.events;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Order;

public class SpongeEventSubscriber<T extends Event> extends AbstractSingleEventSubscriber<T> {
    private Order order;

    private EventListener<? super T> listener;

    public SpongeEventSubscriber(Object plugin, Class<T> event, Order order, boolean beforeModifications) {
        super(event);

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null.");
        }
        if (order == null) {
            throw new IllegalArgumentException("order cannot be null.");
        }

        this.order = order;

        listener = e -> {
            try {
                call(e, order);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException("Could not call event handler.", ex);
            }
        };

        Sponge.getEventManager().registerListener(plugin, event, order, beforeModifications, listener);
    }

    public synchronized void call(T event) { throw new UnsupportedOperationException(); }

    public synchronized void call(T event, Order order) throws Exception {
        if (order == null) {
            throw new IllegalArgumentException("order cannot be null.");
        }

        if (order != this.order) {
            return;
        }
        super.call(event);
    }

    public void cancel() {
        super.cancel();
        Sponge.getEventManager().unregisterListeners(listener);
    }

    public SpongeEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (SpongeEventSubscriber<T>) super.expireAfter(duration, unit); }

    public SpongeEventSubscriber<T> expireAfterCalls(long calls) { return (SpongeEventSubscriber<T>) super.expireAfterCalls(calls); }

    public SpongeEventSubscriber<T> expireIf(Predicate<T> predicate) { return (SpongeEventSubscriber<T>) super.expireIf(predicate); }

    public SpongeEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (SpongeEventSubscriber<T>) super.expireIf(predicate, stages); }

    public SpongeEventSubscriber<T> expireIf(BiPredicate<SpongeEventSubscriber<T>, T> predicate) { return (SpongeEventSubscriber<T>) super.expireIfBi(predicate); }

    public SpongeEventSubscriber<T> expireIf(BiPredicate<SpongeEventSubscriber<T>, T> predicate, TestStage... stages) { return (SpongeEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public SpongeEventSubscriber<T> filter(Predicate<T> predicate) { return (SpongeEventSubscriber<T>) super.filter(predicate); }

    public SpongeEventSubscriber<T> filter(BiPredicate<SpongeEventSubscriber<T>, T> predicate) { return (SpongeEventSubscriber<T>) super.filterBi(predicate); }

    public SpongeEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (SpongeEventSubscriber<T>) super.exceptionHandler(consumer); }

    public SpongeEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (SpongeEventSubscriber<T>) super.exceptionHandler(consumer); }

    public SpongeEventSubscriber<T> handler(Consumer<? super T> handler) { return (SpongeEventSubscriber<T>) super.handler(handler); }

    public SpongeEventSubscriber<T> handler(BiConsumer<SpongeEventSubscriber<T>, ? super T> handler) { return (SpongeEventSubscriber<T>) super.handlerBi(handler); }
}
