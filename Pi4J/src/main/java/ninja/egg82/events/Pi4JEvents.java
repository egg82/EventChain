package ninja.egg82.events;

import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import org.jetbrains.annotations.NotNull;

public class Pi4JEvents {
    private Pi4JEvents() { }

    public static <T extends GpioPinDigitalStateChangeEvent> @NotNull Pi4JDigitalEventSubscriber<T> subscribe(@NotNull GpioPinDigitalInput input, @NotNull Class<T> event) { return new Pi4JDigitalEventSubscriber<>(input, event); }

    public static <T extends GpioPinAnalogValueChangeEvent> @NotNull Pi4JAnalogEventSubscriber<T> subscribe(@NotNull GpioPinAnalogInput input, @NotNull Class<T> event) { return new Pi4JAnalogEventSubscriber<>(input, event); }

    public static <E1 extends GpioPinDigitalStateChangeEvent, T> @NotNull Pi4JMergedDigitalEventSubscriber<E1, T> merge(@NotNull GpioPinDigitalInput input, @NotNull Class<T> superclass) { return new Pi4JMergedDigitalEventSubscriber<>(input, superclass); }

    public static <E1 extends GpioPinAnalogValueChangeEvent, T> @NotNull Pi4JMergedAnalogEventSubscriber<E1, T> merge(@NotNull GpioPinAnalogInput input, @NotNull Class<T> superclass) { return new Pi4JMergedAnalogEventSubscriber<>(input, superclass); }

    public static <E1 extends T, T extends GpioPinDigitalStateChangeEvent> Pi4JMergedDigitalEventSubscriber<E1, T> merge(@NotNull GpioPinDigitalInput input, @NotNull Class<T> superclass, @NotNull Class<E1>... events) {
        Pi4JMergedDigitalEventSubscriber<E1, T> subscriber = new Pi4JMergedDigitalEventSubscriber<>(input, superclass);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }

    public static <E1 extends T, T extends GpioPinAnalogValueChangeEvent> @NotNull Pi4JMergedAnalogEventSubscriber<E1, T> merge(@NotNull GpioPinAnalogInput input, @NotNull Class<T> superclass, @NotNull Class<E1>... events) {
        Pi4JMergedAnalogEventSubscriber<E1, T> subscriber = new Pi4JMergedAnalogEventSubscriber<>(input, superclass);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }
}
