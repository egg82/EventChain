package ninja.egg82.events;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;

public class MergedDiscord4JEventSubscriber<T> extends MergedEventSubscriber<T> implements IListener<Event> {
    private final EventDispatcher dispatcher;

    private ConcurrentMap<Class<? extends Event>, Function<Event, T>> mappings = new ConcurrentHashMap<>();

    public MergedDiscord4JEventSubscriber(EventDispatcher dispatcher, Class<T> commonClass) {
        super(commonClass);

        if (dispatcher == null) {
            throw new IllegalArgumentException("dispatcher cannot be null.");
        }

        this.dispatcher = dispatcher;
        dispatcher.registerListener(this);
    }

    public <E extends Event> MergedDiscord4JEventSubscriber<T> bind(Class<E> event, Function<E, T> function) {
        if (event == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }
        if (function == null) {
            throw new IllegalArgumentException("function cannot be null.");
        }

        mappings.put(event, (Function<Event, T>) function);

        return this;
    }

    public void handle(Event e) {
        try {
            call(e);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Could not call event handler.", ex);
        }
    }

    public synchronized void call(Event event) throws Exception {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }

        if (cancelled) {
            return;
        }

        Function<Event, T> mapping = mappings.get(event.getClass());
        if (mapping == null) {
            return;
        }

        callCount++;

        if (expired) {
            return;
        }

        T obj = mapping.apply(event);

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
        dispatcher.unregisterListener(this);
    }

    public MergedDiscord4JEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (MergedDiscord4JEventSubscriber<T>) super.expireAfter(duration, unit); }

    public MergedDiscord4JEventSubscriber<T> expireAfterCalls(long calls) { return (MergedDiscord4JEventSubscriber<T>) super.expireAfterCalls(calls); }

    public MergedDiscord4JEventSubscriber<T> expireIf(Predicate<T> predicate) { return (MergedDiscord4JEventSubscriber<T>) super.expireIf(predicate); }

    public MergedDiscord4JEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (MergedDiscord4JEventSubscriber<T>) super.expireIf(predicate, stages); }

    public MergedDiscord4JEventSubscriber<T> expireIf(BiPredicate<MergedDiscord4JEventSubscriber<T>, T> predicate) { return (MergedDiscord4JEventSubscriber<T>) super.expireIfBi(predicate); }

    public MergedDiscord4JEventSubscriber<T> expireIf(BiPredicate<MergedDiscord4JEventSubscriber<T>, T> predicate, TestStage... stages) { return (MergedDiscord4JEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public MergedDiscord4JEventSubscriber<T> filter(Predicate<T> predicate) { return (MergedDiscord4JEventSubscriber<T>) super.filter(predicate); }

    public MergedDiscord4JEventSubscriber<T> filter(BiPredicate<MergedDiscord4JEventSubscriber<T>, T> predicate) { return (MergedDiscord4JEventSubscriber<T>) super.filterBi(predicate); }

    public MergedDiscord4JEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (MergedDiscord4JEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedDiscord4JEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (MergedDiscord4JEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedDiscord4JEventSubscriber<T> handler(Consumer<? super T> handler) { return (MergedDiscord4JEventSubscriber<T>) super.handler(handler); }

    public MergedDiscord4JEventSubscriber<T> handler(BiConsumer<MergedDiscord4JEventSubscriber<T>, ? super T> handler) { return (MergedDiscord4JEventSubscriber<T>) super.handlerBi(handler); }
}
