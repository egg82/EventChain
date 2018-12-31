package ninja.egg82.events;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.egg82.events.internal.BungeeAllEventsListener;

public class BungeeEventSubscriber<T extends Event> extends SingleEventSubscriber<T> {
    private final byte priority;

    private final Plugin plugin;
    private final BungeeAllEventsListener<T> listener;

    public BungeeEventSubscriber(Plugin plugin, Class<T> event, byte priority) {
        super(event);

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null.");
        }

        this.priority = priority;
        this.plugin = plugin;

        listener = new BungeeAllEventsListener<>(this);
        plugin.getProxy().getPluginManager().registerListener(plugin, listener);
    }

    public synchronized void call(T event) { throw new UnsupportedOperationException(); }

    public synchronized void call(T event, byte priority) throws Exception {
        if (priority != this.priority) {
            return;
        }
        super.call(event);
    }

    public Class<T> getEventClass() { return super.getEventClass(); }

    public void cancel() {
        super.cancel();
        plugin.getProxy().getPluginManager().unregisterListener(listener);
    }

    public BungeeEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (BungeeEventSubscriber<T>) super.expireAfter(duration, unit); }

    public BungeeEventSubscriber<T> expireAfterCalls(long calls) { return (BungeeEventSubscriber<T>) super.expireAfterCalls(calls); }

    public BungeeEventSubscriber<T> expireIf(Predicate<T> predicate) { return (BungeeEventSubscriber<T>) super.expireIf(predicate); }

    public BungeeEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (BungeeEventSubscriber<T>) super.expireIf(predicate, stages); }

    public BungeeEventSubscriber<T> expireIf(BiPredicate<BungeeEventSubscriber<T>, T> predicate) { return (BungeeEventSubscriber<T>) super.expireIfBi(predicate); }

    public BungeeEventSubscriber<T> expireIf(BiPredicate<BungeeEventSubscriber<T>, T> predicate, TestStage... stages) { return (BungeeEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public BungeeEventSubscriber<T> filter(Predicate<T> predicate) { return (BungeeEventSubscriber<T>) super.filter(predicate); }

    public BungeeEventSubscriber<T> filter(BiPredicate<BungeeEventSubscriber<T>, T> predicate) { return (BungeeEventSubscriber<T>) super.filterBi(predicate); }

    public BungeeEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (BungeeEventSubscriber<T>) super.exceptionHandler(consumer); }

    public BungeeEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (BungeeEventSubscriber<T>) super.exceptionHandler(consumer); }

    public BungeeEventSubscriber<T> handler(Consumer<? super T> handler) { return (BungeeEventSubscriber<T>) super.handler(handler); }

    public BungeeEventSubscriber<T> handler(BiConsumer<BungeeEventSubscriber<T>, ? super T> handler) { return (BungeeEventSubscriber<T>) super.handlerBi(handler); }
}
