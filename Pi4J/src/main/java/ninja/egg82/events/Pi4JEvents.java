package ninja.egg82.events;

import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;

public class Pi4JEvents {
    private Pi4JEvents() { }

    public static <T extends GpioPinDigitalStateChangeEvent> Pi4JDigitalEventSubscriber<T> subscribe(GpioPinDigitalInput input, Class<T> event) { return new Pi4JDigitalEventSubscriber<>(input, event); }

    public static <T extends GpioPinAnalogValueChangeEvent> Pi4JAnalogEventSubscriber<T> subscribe(GpioPinAnalogInput input, Class<T> event) { return new Pi4JAnalogEventSubscriber<>(input, event); }

    public static <T> MergedPi4JDigitalEventSubscriber<T> merge(GpioPinDigitalInput input, Class<T> superclass) { return new MergedPi4JDigitalEventSubscriber<>(input, superclass); }

    public static <T> MergedPi4JAnalogEventSubscriber<T> merge(GpioPinAnalogInput input, Class<T> superclass) { return new MergedPi4JAnalogEventSubscriber<>(input, superclass); }

    public static <T extends GpioPinDigitalStateChangeEvent> MergedPi4JDigitalEventSubscriber<T> merge(GpioPinDigitalInput input, Class<T> superclass, Class<? extends T>... events) {
        MergedPi4JDigitalEventSubscriber<T> subscriber = new MergedPi4JDigitalEventSubscriber<>(input, superclass);
        for (Class<? extends T> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }

    public static <T extends GpioPinAnalogValueChangeEvent> MergedPi4JAnalogEventSubscriber<T> mergeAnalog(GpioPinAnalogInput input, Class<T> superclass, Class<? extends T>... events) {
        MergedPi4JAnalogEventSubscriber<T> subscriber = new MergedPi4JAnalogEventSubscriber<>(input, superclass);
        for (Class<? extends T> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }
}
