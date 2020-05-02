package ninja.egg82.events;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class JDAEventSubscriber<T extends GenericEvent> extends SingleEventSubscriber<T> implements EventListener {
    private final JDA jda;

    public JDAEventSubscriber(JDA jda, Class<T> event) {
        super(event);

        if (jda == null) {
            throw new IllegalArgumentException("jda cannot be null.");
        }

        this.jda = jda;
        jda.addEventListener(this);
    }

    public void onEvent(GenericEvent e) {
        if (!e.getClass().equals(getEventClass()) && !e.getClass().isInstance(getEventClass())) {
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
        jda.removeEventListener(this);
    }

    public JDAEventSubscriber<T> expireAfter(long duration, TimeUnit unit) { return (JDAEventSubscriber<T>) super.expireAfter(duration, unit); }

    public JDAEventSubscriber<T> expireAfterCalls(long calls) { return (JDAEventSubscriber<T>) super.expireAfterCalls(calls); }

    public JDAEventSubscriber<T> expireIf(Predicate<T> predicate) { return (JDAEventSubscriber<T>) super.expireIf(predicate); }

    public JDAEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) { return (JDAEventSubscriber<T>) super.expireIf(predicate, stages); }

    public JDAEventSubscriber<T> expireIf(BiPredicate<JDAEventSubscriber<T>, T> predicate) { return (JDAEventSubscriber<T>) super.expireIfBi(predicate); }

    public JDAEventSubscriber<T> expireIf(BiPredicate<JDAEventSubscriber<T>, T> predicate, TestStage... stages) { return (JDAEventSubscriber<T>) super.expireIfBi(predicate, stages); }

    public JDAEventSubscriber<T> filter(Predicate<T> predicate) { return (JDAEventSubscriber<T>) super.filter(predicate); }

    public JDAEventSubscriber<T> filter(BiPredicate<JDAEventSubscriber<T>, T> predicate) { return (JDAEventSubscriber<T>) super.filterBi(predicate); }

    public JDAEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) { return (JDAEventSubscriber<T>) super.exceptionHandler(consumer); }

    public JDAEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) { return (JDAEventSubscriber<T>) super.exceptionHandler(consumer); }

    public JDAEventSubscriber<T> handler(Consumer<? super T> handler) { return (JDAEventSubscriber<T>) super.handler(handler); }

    public JDAEventSubscriber<T> handler(BiConsumer<JDAEventSubscriber<T>, ? super T> handler) { return (JDAEventSubscriber<T>) super.handlerBi(handler); }
}
