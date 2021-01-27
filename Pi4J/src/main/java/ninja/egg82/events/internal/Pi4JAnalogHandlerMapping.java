package ninja.egg82.events.internal;

import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class Pi4JAnalogHandlerMapping<E extends GpioPinAnalogValueChangeEvent, T> extends AbstractHandlerMapping<E, T> {
    public Pi4JAnalogHandlerMapping(@NotNull Function<E, T> function) {
        super(function);
    }
}
