package ninja.egg82.events;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;

public class Discord4JEventSubscriber<T extends Event> extends SingleEventSubscriber<T> implements IListener<T> {
    private final EventDispatcher dispatcher;

    public Discord4JEventSubscriber(EventDispatcher dispatcher, Class<T> event) {
        super(event);

        if (dispatcher == null) {
            throw new IllegalArgumentException("dispatcher cannot be null.");
        }

        this.dispatcher = dispatcher;
        dispatcher.registerListener(this);
    }

    public void handle(Event e) {
        try {
            call((T) e);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Could not call event handler.", ex);
        }
    }

    public synchronized void call(T event) throws Exception {
        super.call(event);
    }

    public void cancel() {
        super.cancel();
        dispatcher.unregisterListener(this);
    }

    public Discord4JEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (Discord4JEventSubscriber<T>) super.expireAfter(duration, unit); }

    public Discord4JEventSubscriber<T> expireAfterCalls(long calls) { return (Discord4JEventSubscriber<T>) super.expireAfterCalls(calls); }

    public Discord4JEventSubscriber<T> expireIf(Predicate<T> predicate) { return (Discord4JEventSubscriber<T>) super.expireIf(predicate); }

    public Discord4JEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (Discord4JEventSubscriber<T>) super.expireIf(predicate, stages); }

    public Discord4JEventSubscriber<T> expireIf(BiPredicate<Discord4JEventSubscriber<T>, T> predicate) { return (Discord4JEventSubscriber<T>) super.expireIfBi(predicate); }

    public Discord4JEventSubscriber<T> expireIf(BiPredicate<Discord4JEventSubscriber<T>, T> predicate, TestStage... stages) { return (Discord4JEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public Discord4JEventSubscriber<T> filter(Predicate<T> predicate) { return (Discord4JEventSubscriber<T>) super.filter(predicate); }

    public Discord4JEventSubscriber<T> filter(BiPredicate<Discord4JEventSubscriber<T>, T> predicate) { return (Discord4JEventSubscriber<T>) super.filterBi(predicate); }

    public Discord4JEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (Discord4JEventSubscriber<T>) super.exceptionHandler(consumer); }

    public Discord4JEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (Discord4JEventSubscriber<T>) super.exceptionHandler(consumer); }

    public Discord4JEventSubscriber<T> handler(Consumer<? super T> handler) { return (Discord4JEventSubscriber<T>) super.handler(handler); }

    public Discord4JEventSubscriber<T> handler(BiConsumer<Discord4JEventSubscriber<T>, ? super T> handler) { return (Discord4JEventSubscriber<T>) super.handlerBi(handler); }
}
