package ninja.egg82.events;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.jetbrains.annotations.NotNull;

public class Pi4JDigitalEventSubscriber<T extends GpioPinDigitalStateChangeEvent> extends AbstractEventSubscriber<Pi4JDigitalEventSubscriber<T>, GpioPinDigitalStateChangeEvent, T> implements GpioPinListenerDigital {
    private final GpioPinDigitalInput input;

    public Pi4JDigitalEventSubscriber(@NotNull GpioPinDigitalInput input, @NotNull Class<T> event) {
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
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
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
