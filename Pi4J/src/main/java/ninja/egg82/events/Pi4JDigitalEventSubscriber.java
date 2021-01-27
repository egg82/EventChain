package ninja.egg82.events;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Pi4JDigitalEventSubscriber<T extends GpioPinDigitalStateChangeEvent> extends AbstractSingleEventSubscriber<T> implements GpioPinListenerDigital {
    private final GpioPinDigitalInput input;

    public Pi4JDigitalEventSubscriber(GpioPinDigitalInput input, Class<T> event) {
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

    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent e) {
        try {
            call((T) e);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Could not call event handler.", ex);
        }
    }

    public Pi4JDigitalEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (Pi4JDigitalEventSubscriber<T>) super.expireAfter(duration, unit); }

    public Pi4JDigitalEventSubscriber<T> expireAfterCalls(long calls) { return (Pi4JDigitalEventSubscriber<T>) super.expireAfterCalls(calls); }

    public Pi4JDigitalEventSubscriber<T> expireIf(Predicate<T> predicate) { return (Pi4JDigitalEventSubscriber<T>) super.expireIf(predicate); }

    public Pi4JDigitalEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (Pi4JDigitalEventSubscriber<T>) super.expireIf(predicate, stages); }

    public Pi4JDigitalEventSubscriber<T> expireIf(BiPredicate<Pi4JDigitalEventSubscriber<T>, T> predicate) { return (Pi4JDigitalEventSubscriber<T>) super.expireIfBi(predicate); }

    public Pi4JDigitalEventSubscriber<T> expireIf(BiPredicate<Pi4JDigitalEventSubscriber<T>, T> predicate, TestStage... stages) { return (Pi4JDigitalEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public Pi4JDigitalEventSubscriber<T> filter(Predicate<T> predicate) { return (Pi4JDigitalEventSubscriber<T>) super.filter(predicate); }

    public Pi4JDigitalEventSubscriber<T> filter(BiPredicate<Pi4JDigitalEventSubscriber<T>, T> predicate) { return (Pi4JDigitalEventSubscriber<T>) super.filterBi(predicate); }

    public Pi4JDigitalEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (Pi4JDigitalEventSubscriber<T>) super.exceptionHandler(consumer); }

    public Pi4JDigitalEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (Pi4JDigitalEventSubscriber<T>) super.exceptionHandler(consumer); }

    public Pi4JDigitalEventSubscriber<T> handler(Consumer<? super T> handler) { return (Pi4JDigitalEventSubscriber<T>) super.handler(handler); }

    public Pi4JDigitalEventSubscriber<T> handler(BiConsumer<Pi4JDigitalEventSubscriber<T>, ? super T> handler) { return (Pi4JDigitalEventSubscriber<T>) super.handlerBi(handler); }
}
