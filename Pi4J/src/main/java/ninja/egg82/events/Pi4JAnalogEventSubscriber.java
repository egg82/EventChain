package ninja.egg82.events;

import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Pi4JAnalogEventSubscriber<T extends GpioPinAnalogValueChangeEvent> extends SingleEventSubscriber<T> implements GpioPinListenerAnalog {
    private final GpioPinAnalogInput input;

    public Pi4JAnalogEventSubscriber(GpioPinAnalogInput input, Class<T> event) {
        super(event);

        if (input == null) {
            throw new IllegalArgumentException("input cannot be null.");
        }

        this.input = input;
        input.addListener(this);
    }

    public synchronized void call(T event) throws Exception {
        super.call(event);
    }

    public void cancel() {
        super.cancel();
        input.removeListener(this);
    }

    public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent e) {
        try {
            call((T) e);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Could not call event handler.", ex);
        }
    }

    public Pi4JAnalogEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (Pi4JAnalogEventSubscriber<T>) super.expireAfter(duration, unit); }

    public Pi4JAnalogEventSubscriber<T> expireAfterCalls(long calls) { return (Pi4JAnalogEventSubscriber<T>) super.expireAfterCalls(calls); }

    public Pi4JAnalogEventSubscriber<T> expireIf(Predicate<T> predicate) { return (Pi4JAnalogEventSubscriber<T>) super.expireIf(predicate); }

    public Pi4JAnalogEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (Pi4JAnalogEventSubscriber<T>) super.expireIf(predicate, stages); }

    public Pi4JAnalogEventSubscriber<T> expireIf(BiPredicate<Pi4JAnalogEventSubscriber<T>, T> predicate) { return (Pi4JAnalogEventSubscriber<T>) super.expireIfBi(predicate); }

    public Pi4JAnalogEventSubscriber<T> expireIf(BiPredicate<Pi4JAnalogEventSubscriber<T>, T> predicate, TestStage... stages) { return (Pi4JAnalogEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public Pi4JAnalogEventSubscriber<T> filter(Predicate<T> predicate) { return (Pi4JAnalogEventSubscriber<T>) super.filter(predicate); }

    public Pi4JAnalogEventSubscriber<T> filter(BiPredicate<Pi4JAnalogEventSubscriber<T>, T> predicate) { return (Pi4JAnalogEventSubscriber<T>) super.filterBi(predicate); }

    public Pi4JAnalogEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (Pi4JAnalogEventSubscriber<T>) super.exceptionHandler(consumer); }

    public Pi4JAnalogEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (Pi4JAnalogEventSubscriber<T>) super.exceptionHandler(consumer); }

    public Pi4JAnalogEventSubscriber<T> handler(Consumer<? super T> handler) { return (Pi4JAnalogEventSubscriber<T>) super.handler(handler); }

    public Pi4JAnalogEventSubscriber<T> handler(BiConsumer<Pi4JAnalogEventSubscriber<T>, ? super T> handler) { return (Pi4JAnalogEventSubscriber<T>) super.handlerBi(handler); }
}
