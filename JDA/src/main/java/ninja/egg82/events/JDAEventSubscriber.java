package ninja.egg82.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class JDAEventSubscriber<T extends GenericEvent> extends AbstractEventSubscriber<GenericEvent, T> implements EventListener {
    private final JDA jda;

    public JDAEventSubscriber(@NotNull JDA jda, @NotNull Class<T> event) {
        super(event);

        this.jda = jda;
        jda.addEventListener(this);
    }

    public void onEvent(@NotNull GenericEvent e) {
        if (!e.getClass().isInstance(baseClass)) {
            return;
        }

        try {
            call((T) e);
        } catch (EventException ex) {
            throw new RuntimeException("Could not call event subscriber.", ex);
        }
    }

    public void cancel() {
        super.cancel();
        jda.removeEventListener(this);
    }
}
