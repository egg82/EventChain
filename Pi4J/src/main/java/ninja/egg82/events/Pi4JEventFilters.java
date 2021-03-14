package ninja.egg82.events;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;

import java.util.function.Predicate;

public class Pi4JEventFilters {
    private Pi4JEventFilters() { }

    private static final Predicate<? extends GpioPinDigitalStateChangeEvent> IS_HIGH = e -> e.getState().isHigh();
    private static final Predicate<? extends GpioPinDigitalStateChangeEvent> IS_LOW = e -> e.getState().isLow();

    /**
     * Returns a predicate which only returns true if the pin state is high
     *
     * @param <T> the event type
     * @return a predicate which only returns true if the pin state is high
     */
    public static <T extends GpioPinDigitalStateChangeEvent> Predicate<T> isHigh() { return (Predicate<T>) IS_HIGH; }

    /**
     * Returns a predicate which only returns true if the pin state is low
     *
     * @param <T> the event type
     * @return a predicate which only returns true if the pin state is low
     */
    public static <T extends GpioPinDigitalStateChangeEvent> Predicate<T> isLow() { return (Predicate<T>) IS_LOW; }
}
