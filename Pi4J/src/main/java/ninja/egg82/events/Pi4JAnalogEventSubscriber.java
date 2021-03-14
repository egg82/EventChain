package ninja.egg82.events;

import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;
import org.jetbrains.annotations.NotNull;

public class Pi4JAnalogEventSubscriber<T extends GpioPinAnalogValueChangeEvent> extends AbstractEventSubscriber<Pi4JAnalogEventSubscriber<T>, GpioPinAnalogValueChangeEvent, T> implements GpioPinListenerAnalog {
    private final GpioPinAnalogInput input;

    public Pi4JAnalogEventSubscriber(@NotNull GpioPinAnalogInput input, @NotNull Class<T> event) {
        super(event);

        this.input = input;
        input.addListener(this);
    }

    @Override
    public void cancel() {
        super.cancel();
        input.removeListener(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
        if (!baseClass.isInstance(event)) {
            return;
        }

        try {
            call((T) event);
        } catch (EventException ex) {
            throw new RuntimeException("Could not call event subscriber.", ex);
        }
    }
}
