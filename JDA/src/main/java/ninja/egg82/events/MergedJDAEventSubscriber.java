package ninja.egg82.events;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class MergedJDAEventSubscriber<T> extends MergedEventSubscriber<T> implements EventListener {
    private final JDA jda;

    private ConcurrentMap<Class<? extends GenericEvent>, Function<GenericEvent, T>> mappings = new ConcurrentHashMap<>();

    public MergedJDAEventSubscriber(JDA jda, Class<T> commonClass) {
        super(commonClass);

        if (jda == null) {
            throw new IllegalArgumentException("jda cannot be null.");
        }

        this.jda = jda;
        jda.addEventListener(this);
    }

    public <E extends GenericEvent> MergedJDAEventSubscriber<T> bind(Class<E> event, Function<E, T> function) {
        if (event == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }
        if (function == null) {
            throw new IllegalArgumentException("function cannot be null.");
        }

        mappings.put(event, (Function<GenericEvent, T>) function);

        return this;
    }

    public void onEvent(GenericEvent e) {
        try {
            call(e);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Could not call event handler.", ex);
        }
    }

    public synchronized void call(GenericEvent event) throws Exception {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }

        if (cancelled) {
            return;
        }

        Function<GenericEvent, T> mapping = mappings.get(event.getClass());
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
        jda.removeEventListener(this);
    }

    public MergedJDAEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (MergedJDAEventSubscriber<T>) super.expireAfter(duration, unit); }

    public MergedJDAEventSubscriber<T> expireAfterCalls(long calls) { return (MergedJDAEventSubscriber<T>) super.expireAfterCalls(calls); }

    public MergedJDAEventSubscriber<T> expireIf(Predicate<T> predicate) { return (MergedJDAEventSubscriber<T>) super.expireIf(predicate); }

    public MergedJDAEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (MergedJDAEventSubscriber<T>) super.expireIf(predicate, stages); }

    public MergedJDAEventSubscriber<T> expireIf(BiPredicate<MergedJDAEventSubscriber<T>, T> predicate) { return (MergedJDAEventSubscriber<T>) super.expireIfBi(predicate); }

    public MergedJDAEventSubscriber<T> expireIf(BiPredicate<MergedJDAEventSubscriber<T>, T> predicate, TestStage... stages) { return (MergedJDAEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public MergedJDAEventSubscriber<T> filter(Predicate<T> predicate) { return (MergedJDAEventSubscriber<T>) super.filter(predicate); }

    public MergedJDAEventSubscriber<T> filter(BiPredicate<MergedJDAEventSubscriber<T>, T> predicate) { return (MergedJDAEventSubscriber<T>) super.filterBi(predicate); }

    public MergedJDAEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (MergedJDAEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedJDAEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (MergedJDAEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedJDAEventSubscriber<T> handler(Consumer<? super T> handler) { return (MergedJDAEventSubscriber<T>) super.handler(handler); }

    public MergedJDAEventSubscriber<T> handler(BiConsumer<MergedJDAEventSubscriber<T>, ? super T> handler) { return (MergedJDAEventSubscriber<T>) super.handlerBi(handler); }
}
