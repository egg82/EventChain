package ninja.egg82.events;

import java.util.function.Function;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import ninja.egg82.events.internal.JDAHandlerMapping;
import org.jetbrains.annotations.NotNull;

public class JDAMergedEventSubscriber<E1 extends GenericEvent, T> extends AbstractMergedEventSubscriber<JDAMergedEventSubscriber<E1, T>, E1, T> implements EventListener {
    private final JDA jda;

    public JDAMergedEventSubscriber(@NotNull JDA jda, @NotNull Class<T> superclass) {
        super(superclass);

        this.jda = jda;
        jda.addEventListener(this);
    }

    public @NotNull JDAMergedEventSubscriber<E1, T> bind(@NotNull Class<E1> event, @NotNull Function<E1, T> function) {
        mappings.put(event, new JDAHandlerMapping<>(function));
        return this;
    }

    public void onEvent(@NotNull GenericEvent event) {
        try {
            callMerged(event);
        } catch (EventException ex) {
            throw new RuntimeException("Could not call merged event subscriber.", ex);
        }
    }

    public void cancel() {
        super.cancel();
        jda.removeEventListener(this);
    }
}
