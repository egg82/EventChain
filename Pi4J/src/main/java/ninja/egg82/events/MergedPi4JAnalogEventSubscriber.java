package ninja.egg82.events;

import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

public class MergedPi4JAnalogEventSubscriber<T> extends AbstractMergedPriorityEventSubscriber<T> implements GpioPinListenerAnalog {
    private final GpioPinAnalogInput input;

    private ConcurrentMap<Class<? extends GpioPinAnalogValueChangeEvent>, Function<GpioPinAnalogValueChangeEvent, T>> mappings = new ConcurrentHashMap<>();

    public MergedPi4JAnalogEventSubscriber(GpioPinAnalogInput input, Class<T> commonClass) {
        super(commonClass);

        if (input == null) {
            throw new IllegalArgumentException("input cannot be null.");
        }

        this.input = input;
        input.addListener(this);
    }

    public <E extends GpioPinAnalogValueChangeEvent> MergedPi4JAnalogEventSubscriber<T> bind(Class<E> event, Function<E, T> function) {
        if (event == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }
        if (function == null) {
            throw new IllegalArgumentException("function cannot be null.");
        }

        mappings.put(event, (Function<GpioPinAnalogValueChangeEvent, T>) function);

        return this;
    }

    public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent e) {
        try {
            call(e);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Could not call event handler.", ex);
        }
    }

    public synchronized void call(GpioPinAnalogValueChangeEvent event) throws Exception {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }

        if (cancelled) {
            return;
        }

        Function<GpioPinAnalogValueChangeEvent, T> mapping = mappings.get(event.getClass());
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

    public MergedPi4JAnalogEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (MergedPi4JAnalogEventSubscriber<T>) super.expireAfter(duration, unit); }

    public MergedPi4JAnalogEventSubscriber<T> expireAfterCalls(long calls) { return (MergedPi4JAnalogEventSubscriber<T>) super.expireAfterCalls(calls); }

    public MergedPi4JAnalogEventSubscriber<T> expireIf(Predicate<T> predicate) { return (MergedPi4JAnalogEventSubscriber<T>) super.expireIf(predicate); }

    public MergedPi4JAnalogEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (MergedPi4JAnalogEventSubscriber<T>) super.expireIf(predicate, stages); }

    public MergedPi4JAnalogEventSubscriber<T> expireIf(BiPredicate<MergedPi4JAnalogEventSubscriber<T>, T> predicate) { return (MergedPi4JAnalogEventSubscriber<T>) super.expireIfBi(predicate); }

    public MergedPi4JAnalogEventSubscriber<T> expireIf(BiPredicate<MergedPi4JAnalogEventSubscriber<T>, T> predicate, TestStage... stages) { return (MergedPi4JAnalogEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public MergedPi4JAnalogEventSubscriber<T> filter(Predicate<T> predicate) { return (MergedPi4JAnalogEventSubscriber<T>) super.filter(predicate); }

    public MergedPi4JAnalogEventSubscriber<T> filter(BiPredicate<MergedPi4JAnalogEventSubscriber<T>, T> predicate) { return (MergedPi4JAnalogEventSubscriber<T>) super.filterBi(predicate); }

    public MergedPi4JAnalogEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (MergedPi4JAnalogEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedPi4JAnalogEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (MergedPi4JAnalogEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedPi4JAnalogEventSubscriber<T> handler(Consumer<? super T> handler) { return (MergedPi4JAnalogEventSubscriber<T>) super.handler(handler); }

    public MergedPi4JAnalogEventSubscriber<T> handler(BiConsumer<MergedPi4JAnalogEventSubscriber<T>, ? super T> handler) { return (MergedPi4JAnalogEventSubscriber<T>) super.handlerBi(handler); }
}
