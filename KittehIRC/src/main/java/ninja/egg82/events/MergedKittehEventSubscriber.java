package ninja.egg82.events;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import net.engio.mbassy.listener.Handler;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.helper.ClientEvent;

public class MergedKittehEventSubscriber<T> extends MergedEventSubscriber<T> {
    private final Client client;

    private ConcurrentMap<Class<? extends ClientEvent>, Function<ClientEvent, T>> mappings = new ConcurrentHashMap<>();

    public MergedKittehEventSubscriber(Client client, Class<T> commonClass) {
        super(commonClass);

        if (client == null) {
            throw new IllegalArgumentException("client cannot be null.");
        }

        this.client = client;
        client.getEventManager().registerEventListener(this);
    }

    public <E extends ClientEvent> MergedKittehEventSubscriber<T> bind(Class<E> event, Function<E, T> function) {
        if (event == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }
        if (function == null) {
            throw new IllegalArgumentException("function cannot be null.");
        }

        mappings.put(event, (Function<ClientEvent, T>) function);

        return this;
    }

    @Handler
    public void onAnyEvent(ClientEvent e) {
        try {
            call(e);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Could not call event handler.", ex);
        }
    }

    public synchronized void call(ClientEvent event) throws Exception {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }

        if (cancelled) {
            return;
        }

        Function<ClientEvent, T> mapping = mappings.get(event.getClass());
        if (mapping == null) {
            return;
        }

        callCount++;

        if (expired) {
            return;
        }

        T obj = mapping.apply(event);

        if (expire(obj, expirePredicates.get(TestStage.PRE_FILTER), expireBiPredicates.get(TestStage.PRE_FILTER))) {
            return;
        }

        if (filter(obj)) {
            return;
        }

        if (expire(obj, expirePredicates.get(TestStage.POST_FILTER), expireBiPredicates.get(TestStage.POST_FILTER))) {
            return;
        }

        for (Consumer<? super T> consumer : handlerConsumers) {
            try {
                consumer.accept(obj);
            } catch (Exception ex) {
                swallowException(obj, ex);
            }
        }
        for (BiConsumer<? extends MergedEventSubscriber<T>, ? super T> consumer : handlerBiConsumers) {
            BiConsumer<MergedEventSubscriber<T>, ? super T> c = (BiConsumer<MergedEventSubscriber<T>, ? super T>) consumer;
            try {
                c.accept(this, obj);
            } catch (Exception ex) {
                swallowException(obj, ex);
            }
        }

        expire(obj, expirePredicates.get(TestStage.POST_HANDLE), expireBiPredicates.get(TestStage.POST_HANDLE));
    }

    public void cancel() {
        super.cancel();
        client.getEventManager().unregisterEventListener(this);
    }

    public MergedKittehEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (MergedKittehEventSubscriber<T>) super.expireAfter(duration, unit); }

    public MergedKittehEventSubscriber<T> expireAfterCalls(long calls) { return (MergedKittehEventSubscriber<T>) super.expireAfterCalls(calls); }

    public MergedKittehEventSubscriber<T> expireIf(Predicate<T> predicate) { return (MergedKittehEventSubscriber<T>) super.expireIf(predicate); }

    public MergedKittehEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (MergedKittehEventSubscriber<T>) super.expireIf(predicate, stages); }

    public MergedKittehEventSubscriber<T> expireIf(BiPredicate<MergedKittehEventSubscriber<T>, T> predicate) { return (MergedKittehEventSubscriber<T>) super.expireIfBi(predicate); }

    public MergedKittehEventSubscriber<T> expireIf(BiPredicate<MergedKittehEventSubscriber<T>, T> predicate, TestStage... stages) { return (MergedKittehEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public MergedKittehEventSubscriber<T> filter(Predicate<T> predicate) { return (MergedKittehEventSubscriber<T>) super.filter(predicate); }

    public MergedKittehEventSubscriber<T> filter(BiPredicate<MergedKittehEventSubscriber<T>, T> predicate) { return (MergedKittehEventSubscriber<T>) super.filterBi(predicate); }

    public MergedKittehEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (MergedKittehEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedKittehEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (MergedKittehEventSubscriber<T>) super.exceptionHandler(consumer); }

    public MergedKittehEventSubscriber<T> handler(Consumer<? super T> handler) { return (MergedKittehEventSubscriber<T>) super.handler(handler); }

    public MergedKittehEventSubscriber<T> handler(BiConsumer<MergedKittehEventSubscriber<T>, ? super T> handler) { return (MergedKittehEventSubscriber<T>) super.handlerBi(handler); }
}
