package ninja.egg82.events;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.util.function.Function;
import ninja.egg82.events.internal.Pi4JDigitalHandlerMapping;
import org.jetbrains.annotations.NotNull;

public class Pi4JMergedDigitalEventSubscriber<E1 extends GpioPinDigitalStateChangeEvent, T> extends AbstractMergedEventSubscriber<E1, T> implements GpioPinListenerDigital {
    private final GpioPinDigitalInput input;

    public Pi4JMergedDigitalEventSubscriber(@NotNull GpioPinDigitalInput input, @NotNull Class<T> superclass) {
        super(superclass);

        this.input = input;
        input.addListener(this);
    }

    public @NotNull MergedEventSubscriber<E1, T> bind(@NotNull Class<E1> event, @NotNull Function<E1, T> function) {
        mappings.put(event, new Pi4JDigitalHandlerMapping<>(function));
        return this;
    }

    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
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
