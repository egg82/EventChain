package ninja.egg82.events.internal;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class Pi4JDigitalHandlerMapping<E extends GpioPinDigitalStateChangeEvent, T> extends AbstractHandlerMapping<E, T> {
    public Pi4JDigitalHandlerMapping(@NotNull Function<E, T> function) {
        super(function);
    }
}
