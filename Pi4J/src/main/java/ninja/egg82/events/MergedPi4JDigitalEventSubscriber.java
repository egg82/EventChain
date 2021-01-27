package ninja.egg82.events;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

public class MergedPi4JDigitalEventSubscriber<T> extends AbstractMergedPriorityEventSubscriber<T> implements GpioPinListenerDigital {
    private final GpioPinDigitalInput input;

    private ConcurrentMap<Class<? extends GpioPinDigitalStateChangeEvent>, Function<GpioPinDigitalStateChangeEvent, T>> mappings = new ConcurrentHashMap<>();

    public MergedPi4JDigitalEventSubscriber(GpioPinDigitalInput input, Class<T> superclass) {
        super(superclass);

        if (input == null) {
            throw new IllegalArgumentException("input cannot be null.");
        }

        this.input = input;
        input.addListener(this);
    }

    public <E extends GpioPinDigitalStateChangeEvent> MergedPi4JDigitalEventSubscriber<T> bind(Class<E> event, Function<E, T> function) {
        if (event == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }
        if (function == null) {
            throw new IllegalArgumentException("function cannot be null.");
        }

        mappings.put(event, (Function<GpioPinDigitalStateChangeEvent, T>) function);

        return this;
    }

    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent e) {
        try {
            call(e);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Could not call event handler.", ex);
        }
    }

    public synchronized void call(GpioPinDigitalStateChangeEvent event) throws Exception {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }

        if (cancelled) {
            return;
        }

        Function<GpioPinDigitalStateChangeEvent, T> mapping = mappings.get(event.getClass());
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
        for (BiConsumer<? extends AbstractMergedPriorityEventSubscriber<T>, ? super T> consumer : handlerBiConsumers) {
            BiConsumer<AbstractMergedPriorityEventSubscriber<T>, ? super T> c = (BiConsumer<AbstractMergedPriorityEventSubscriber<T>, ? super T>) consumer;
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
        input.removeListener(this);
    }

    public MergedPi4JDigitalEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (MergedPi4JDigitalEventSubscriber<T>) super.expireAfter(duration, unit); }

    public MergedPi4JDigitalEventSubscriber<T> expireAfterCalls(long calls) { return (MergedPi4JDigitalEventSubscriber<T>) super.expireAfterCalls(calls); }

    public MergedPi4JDigitalEventSubscriber<T> expireIf(Predicate<T> predicate) { return (MergedPi4JDigitalEventSubscriber<T>) super.expireIf(predicate); }

    public MergedPi4JDigitalEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (MergedPi4JDigitalEventSubscriber<T>) super.expireIf(predicate, stages); }

    public MergedPi4JDigitalEventSubscriber<T> expireIf(BiPredicate<MergedPi4JDigitalEventSubscriber<T>, T> predicate) { return (MergedPi4JDigitalEventSubscriber<T>) super.expireIfBi(predicate); }

    public MergedPi4JDigitalEventSubscriber<T> expireIf(BiPredicate<MergedPi4JDigitalEventSubscriber<T>, T> predicate, TestStage... stages) { return (MergedPi4JDigitalEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public MergedPi4JDigitalEventSubscriber<T> filter(Predicate<T> predicate) { return (MergedPi4JDigitalEventSubscriber<T>) super.filter(predicate); }

    public MergedPi4JDigitalEventSubscriber<T> filter(BiPredicate<MergedPi4JDigitalEventSubscriber<T>, T> predicate) { return (MergedPi4JDigitalEventSubscriber<T>) super.filterBi(predicate); }

    public MergedPi4JDigitalEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (MergedPi4JDigitalEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedPi4JDigitalEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (MergedPi4JDigitalEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedPi4JDigitalEventSubscriber<T> handler(Consumer<? super T> handler) { return (MergedPi4JDigitalEventSubscriber<T>) super.handler(handler); }

    public MergedPi4JDigitalEventSubscriber<T> handler(BiConsumer<MergedPi4JDigitalEventSubscriber<T>, ? super T> handler) { return (MergedPi4JDigitalEventSubscriber<T>) super.handlerBi(handler); }
}
