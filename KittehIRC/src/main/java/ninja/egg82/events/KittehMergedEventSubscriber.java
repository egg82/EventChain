package ninja.egg82.events;

import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;
import ninja.egg82.events.internal.KittehHandlerMapping;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.helper.ClientEvent;

import java.util.function.Function;

@Listener(references = References.Strong)
public class KittehMergedEventSubscriber<E1 extends ClientEvent, T> extends AbstractMergedEventSubscriber<KittehMergedEventSubscriber<E1, T>, E1, T> {
    private final Client client;

    public KittehMergedEventSubscriber(@NotNull Client client, @NotNull Class<T> superclass) {
        super(superclass);

        this.client = client;
        client.getEventManager().registerEventListener(this);
    }

    @Override
    public @NotNull KittehMergedEventSubscriber<E1, T> bind(@NotNull Class<E1> event, @NotNull Function<E1, T> function) {
        mappings.put(event, new KittehHandlerMapping<>(function));
        return this;
    }

    @Handler
    public void onAnyEvent(ClientEvent event) {
        try {
            callMerged(event);
        } catch (EventException ex) {
            throw new RuntimeException("Could not call merged event subscriber.", ex);
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        client.getEventManager().unregisterEventListener(this);
    }
}
