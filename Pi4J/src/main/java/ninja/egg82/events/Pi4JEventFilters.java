package ninja.egg82.events;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;

import java.util.function.Predicate;

public class Pi4JEventFilters {
    private Pi4JEventFilters() { }

    /**
     * Returns a predicate which only returns true if the pin state is high
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the pin state is high
     */
    public static <T extends GpioPinDigitalStateChangeEvent> Predicate<T> isHigh() { return e -> e.getState().isHigh(); }

    /**
     * Returns a predicate which only returns true if the pin state is low
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the pin state is low
     */
    public static <T extends GpioPinDigitalStateChangeEvent> Predicate<T> isLow() { return e -> e.getState().isLow(); }
}
