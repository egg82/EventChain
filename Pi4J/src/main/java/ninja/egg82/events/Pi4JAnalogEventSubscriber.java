package ninja.egg82.events;

import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public class Pi4JAnalogEventSubscriber<T extends GpioPinAnalogValueChangeEvent> extends AbstractEventSubscriber<GpioPinAnalogValueChangeEvent, T> implements GpioPinListenerAnalog {
    private final GpioPinAnalogInput input;

    public Pi4JAnalogEventSubscriber(@NotNull GpioPinAnalogInput input, @NotNull Class<T> event) {
        super(event);

        this.input = input;
        input.addListener(this);
    }

    public void cancel() {
        super.cancel();
        input.removeListener(this);
    }

    public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
        try {
            call((T) event);
        } catch (EventException ex) {
            throw new RuntimeException("Could not call event subscriber.", ex);
        }
    }
}
