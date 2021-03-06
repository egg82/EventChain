package ninja.egg82.events;

import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.helper.ClientEvent;

@Listener(references = References.Strong)
public class KittehEventSubscriber<T extends ClientEvent> extends AbstractEventSubscriber<KittehEventSubscriber<T>, ClientEvent, T> {
    private final Client client;

    public KittehEventSubscriber(@NotNull Client client, @NotNull Class<T> event) {
        super(event);

        this.client = client;
        client.getEventManager().registerEventListener(this);
    }

    @Handler
    @SuppressWarnings("unchecked")
    public void onAnyEvent(@NotNull ClientEvent event) {
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
        client.getEventManager().unregisterEventListener(this);
    }
}
