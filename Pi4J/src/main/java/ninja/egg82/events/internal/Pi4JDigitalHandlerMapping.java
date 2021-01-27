package ninja.egg82.events.internal;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class Pi4JDigitalHandlerMapping<E extends GpioPinDigitalStateChangeEvent, T> extends AbstractHandlerMapping<E, T> {
    public Pi4JDigitalHandlerMapping(@NotNull Function<E, T> function) {
        super(function);
    }
}
