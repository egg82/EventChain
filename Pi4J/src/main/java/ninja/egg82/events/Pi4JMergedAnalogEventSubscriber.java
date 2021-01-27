package ninja.egg82.events;

import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;
import java.util.function.Function;
import ninja.egg82.events.internal.Pi4JAnalogHandlerMapping;
import org.jetbrains.annotations.NotNull;

public class Pi4JMergedAnalogEventSubscriber<E1 extends GpioPinAnalogValueChangeEvent, T> extends AbstractMergedEventSubscriber<E1, T> implements GpioPinListenerAnalog {
    private final GpioPinAnalogInput input;

    public Pi4JMergedAnalogEventSubscriber(@NotNull GpioPinAnalogInput input, @NotNull Class<T> superclass) {
        super(superclass);

        this.input = input;
        input.addListener(this);
    }

    public @NotNull MergedEventSubscriber<E1, T> bind(@NotNull Class<E1> event, @NotNull Function<E1, T> function) {
        mappings.put(event, new Pi4JAnalogHandlerMapping<>(function));
        return this;
    }

    public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
        try {
            callMerged(event);
        } catch (EventException ex) {
            throw new RuntimeException("Could not call merged event subscriber.", ex);
        }
    }

    public void cancel() {
        super.cancel();
        input.removeListener(this);
    }
}
