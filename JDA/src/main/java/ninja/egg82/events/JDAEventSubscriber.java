package ninja.egg82.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class JDAEventSubscriber<T extends GenericEvent> extends AbstractEventSubscriber<JDAEventSubscriber<T>, GenericEvent, T> implements EventListener {
    private final JDA jda;

    public JDAEventSubscriber(@NotNull JDA jda, @NotNull Class<T> event) {
        super(event);

        this.jda = jda;
        jda.addEventListener(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onEvent(@NotNull GenericEvent event) {
        if (!baseClass.isInstance(event)) {
            return;
        }

        try {
            call((T) event);
        } catch (EventException ex) {
            throw new RuntimeException("Could not call event subscriber.", ex);
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        jda.removeEventListener(this);
    }
}
