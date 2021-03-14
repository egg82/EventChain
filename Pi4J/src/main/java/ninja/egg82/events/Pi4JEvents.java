package ninja.egg82.events;

import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import org.jetbrains.annotations.NotNull;

public class Pi4JEvents {
    private Pi4JEvents() {
    }

    /**
     * Returns a single event subscriber.
     *
     * @param input the input to listen to events with
     * @param event the event class to listen to
     *
     * @return a new {@link Pi4JDigitalEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code input} or {@code event} is null
     */
    public static <T extends GpioPinDigitalStateChangeEvent> @NotNull Pi4JDigitalEventSubscriber<T> subscribe(
            @NotNull GpioPinDigitalInput input,
            @NotNull Class<T> event
    ) {
        return new Pi4JDigitalEventSubscriber<>(input, event);
    }

    /**
     * Returns a single event subscriber.
     *
     * @param input the input to listen to events with
     * @param event the event class to listen to
     *
     * @return a new {@link Pi4JAnalogEventSubscriber} that listens to the desired event
     *
     * @throws NullPointerException if {@code input} or {@code event} is null
     */
    public static <T extends GpioPinAnalogValueChangeEvent> @NotNull Pi4JAnalogEventSubscriber<T> subscribe(@NotNull GpioPinAnalogInput input, @NotNull Class<T> event) {
        return new Pi4JAnalogEventSubscriber<>(input, event);
    }

    /**
     * Returns a merged event subscriber.
     *
     * @param input the input to listen to events with
     * @param superclass the event class that will be processed in the handler
     *
     * @return a new {@link Pi4JMergedDigitalEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code input} or {@code superclass} is null
     */
    public static <E1 extends GpioPinDigitalStateChangeEvent, T> @NotNull Pi4JMergedDigitalEventSubscriber<E1, T> merge(
            @NotNull GpioPinDigitalInput input,
            @NotNull Class<T> superclass
    ) {
        return new Pi4JMergedDigitalEventSubscriber<>(input, superclass);
    }

    /**
     * Returns a merged event subscriber.
     *
     * @param input the input to listen to events with
     * @param superclass the event class that will be processed in the handler
     *
     * @return a new {@link Pi4JMergedAnalogEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code input} or {@code superclass} is null
     */
    public static <E1 extends GpioPinAnalogValueChangeEvent, T> @NotNull Pi4JMergedAnalogEventSubscriber<E1, T> merge(
            @NotNull GpioPinAnalogInput input,
            @NotNull Class<T> superclass
    ) {
        return new Pi4JMergedAnalogEventSubscriber<>(input, superclass);
    }

    /**
     * Returns a merged event subscriber
     * that listens to multiple similar events.
     *
     * @param input the input to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @param events the events to listen to
     *
     * @return a new {@link Pi4JMergedDigitalEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code input}, {@code superclass}, or {@code events} are null
     */
    public static <E1 extends T, T extends GpioPinDigitalStateChangeEvent> Pi4JMergedDigitalEventSubscriber<E1, T> merge(
            @NotNull GpioPinDigitalInput input,
            @NotNull Class<T> superclass,
            @NotNull Class<E1>... events
    ) {
        Pi4JMergedDigitalEventSubscriber<E1, T> subscriber = new Pi4JMergedDigitalEventSubscriber<>(input, superclass);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }

    /**
     * Returns a merged event subscriber
     * that listens to multiple similar events.
     *
     * @param input the input to listen to events with
     * @param superclass the event class that will be processed in the handler
     * @param events the events to listen to
     *
     * @return a new {@link Pi4JMergedAnalogEventSubscriber} that listens to the desired events
     *
     * @throws NullPointerException if {@code input}, {@code superclass}, or {@code events} are null
     */
    public static <E1 extends T, T extends GpioPinAnalogValueChangeEvent> @NotNull Pi4JMergedAnalogEventSubscriber<E1, T> merge(
            @NotNull GpioPinAnalogInput input,
            @NotNull Class<T> superclass,
            @NotNull Class<E1>... events
    ) {
        Pi4JMergedAnalogEventSubscriber<E1, T> subscriber = new Pi4JMergedAnalogEventSubscriber<>(input, superclass);
        for (Class<E1> clazz : events) {
            subscriber.bind(clazz, e -> e);
        }
        return subscriber;
    }
}
