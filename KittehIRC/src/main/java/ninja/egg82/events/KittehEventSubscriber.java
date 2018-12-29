package ninja.egg82.events;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.engio.mbassy.listener.Handler;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.helper.ClientEvent;

public class KittehEventSubscriber<T extends ClientEvent> extends SingleEventSubscriber<T> {
    private final Client client;

    public KittehEventSubscriber(Client client, Class<T> event) {
        super(event);

        if (client == null) {
            throw new IllegalArgumentException("client cannot be null.");
        }

        this.client = client;
        client.getEventManager().registerEventListener(this);
    }

    @Handler
    public void onAnyEvent(ClientEvent e) {
        if (!e.getClass().isInstance(getEventClass())) {
            return;
        }

        try {
            call((T) e);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Could not call event handler.", ex);
        }
    }

    public synchronized void call(T event) throws Exception {
        super.call(event);
    }

    public void cancel() {
        super.cancel();
        client.getEventManager().unregisterEventListener(this);
    }

    public KittehEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (KittehEventSubscriber<T>) super.expireAfter(duration, unit); }

    public KittehEventSubscriber<T> expireAfterCalls(long calls) { return (KittehEventSubscriber<T>) super.expireAfterCalls(calls); }

    public KittehEventSubscriber<T> expireIf(Predicate<T> predicate) { return (KittehEventSubscriber<T>) super.expireIf(predicate); }

    public KittehEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (KittehEventSubscriber<T>) super.expireIf(predicate, stages); }

    public KittehEventSubscriber<T> expireIf(BiPredicate<KittehEventSubscriber<T>, T> predicate) { return (KittehEventSubscriber<T>) super.expireIfBi(predicate); }

    public KittehEventSubscriber<T> expireIf(BiPredicate<KittehEventSubscriber<T>, T> predicate, TestStage... stages) { return (KittehEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public KittehEventSubscriber<T> filter(Predicate<T> predicate) { return (KittehEventSubscriber<T>) super.filter(predicate); }

    public KittehEventSubscriber<T> filter(BiPredicate<KittehEventSubscriber<T>, T> predicate) { return (KittehEventSubscriber<T>) super.filterBi(predicate); }

    public KittehEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (KittehEventSubscriber<T>) super.exceptionHandler(consumer); }

    public KittehEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (KittehEventSubscriber<T>) super.exceptionHandler(consumer); }

    public KittehEventSubscriber<T> handler(Consumer<? super T> handler) { return (KittehEventSubscriber<T>) super.handler(handler); }

    public KittehEventSubscriber<T> handler(BiConsumer<KittehEventSubscriber<T>, ? super T> handler) { return (KittehEventSubscriber<T>) super.handlerBi(handler); }
}
