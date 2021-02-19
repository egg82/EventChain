package ninja.egg82.events;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractEventSubscriber<S extends AbstractEventSubscriber<S, E, T>, E, T> implements EventSubscriber<S, T> {
    protected final Class<T> baseClass;

    protected final ConcurrentMap<TestStage, List<BiPredicate<AbstractEventSubscriber<S, E, T>, T>>> expireBiPredicates = new ConcurrentHashMap<>();
    protected final List<BiConsumer<AbstractEventSubscriber<S, E, T>, ? super T>> handlerBiConsumers = new CopyOnWriteArrayList<>();
    private final List<BiPredicate<AbstractEventSubscriber<S, E, T>, T>> filterBiPredicates = new CopyOnWriteArrayList<>();
    private final List<BiConsumer<? super T, Throwable>> exceptionBiConsumers = new CopyOnWriteArrayList<>();

    protected final Lock syncLock = new ReentrantLock();

    protected AbstractEventSubscriber(Class<T> baseClass) {
        this.baseClass = baseClass;
    }

    public Class<T> getBaseClass() { return baseClass; }

    protected AtomicBoolean cancelState = new AtomicBoolean(false);

    public @NotNull AtomicBoolean cancellationState() { return cancelState; }

    protected AtomicBoolean expireState = new AtomicBoolean(false);

    public @NotNull AtomicBoolean expirationState() { return expireState; }

    protected AtomicLong calls = new AtomicLong(0L);

    public @NotNull AtomicLong callCount() { return calls; }

    public void cancel() {
        cancelState.set(true);
    }

    public void call(@NotNull T event) throws EventException {
        if (cancelState.get()) {
            return;
        }

        calls.incrementAndGet();

        if (expireState.get()) {
            return;
        }

        syncLock.lock();
        try {
            if (tryExpire(event, expireBiPredicates.get(TestStage.PRE_FILTER), SubscriberStage.PRE_FILTER_EXPIRE)) {
                expireState.set(true);
                return;
            }

            if (filter(event)) {
                return;
            }

            if (tryExpire(event, expireBiPredicates.get(TestStage.POST_FILTER), SubscriberStage.POST_FILTER_EXPIRE)) {
                expireState.set(true);
                return;
            }

            for (BiConsumer<AbstractEventSubscriber<S, E, T>, ? super T> consumer : handlerBiConsumers) {
                try {
                    consumer.accept(this, event);
                } catch (Exception ex) {
                    swallowException(event, ex, SubscriberStage.HANDLE);
                }
            }

            if (tryExpire(event, expireBiPredicates.get(TestStage.POST_HANDLE), SubscriberStage.POST_HANDLE_EXPIRE)) {
                expireState.set(true);
            }
        } finally {
            syncLock.unlock();
        }
    }

    public @NotNull S expireAfter(long duration, @NotNull TimeUnit unit) {
        if (duration < 0L) {
            throw new IllegalArgumentException("duration cannot be negative: " + duration + " " + unit);
        }

        long expireTime = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
        return expireIf((h, e) -> System.currentTimeMillis() > expireTime, TestStage.PRE_FILTER);
    }

    public @NotNull S expireAfterCalls(long calls) {
        if (calls < 0L) {
            throw new IllegalArgumentException("calls cannot be negative: " + calls);
        }

        return expireIf((h, e) -> getCallCount() >= calls, TestStage.PRE_FILTER, TestStage.POST_HANDLE);
    }

    public @NotNull S expireIf(@NotNull Predicate<T> predicate, @NotNull TestStage... stages) { return expireIf((t, p) -> predicate.test(p), stages); }

    private @NotNull S expireIf(@NotNull BiPredicate<AbstractEventSubscriber<S, E, T>, T> predicate, @NotNull TestStage... stages) {
        for (TestStage stage : stages) {
            expireBiPredicates.compute(stage, (k, v) -> {
                if (v == null) {
                    v = new CopyOnWriteArrayList<>();
                }
                v.add(predicate);
                return v;
            });
        }
        return (S) this;
    }

    public @NotNull S filter(@NotNull Predicate<T> predicate) { return filter((t, p) -> predicate.test(p)); }

    private @NotNull S filter(@NotNull BiPredicate<AbstractEventSubscriber<S, E, T>, T> predicate) {
        filterBiPredicates.add(predicate);
        return (S) this;
    }

    public @NotNull S exceptionHandler(@NotNull Consumer<Throwable> consumer) { return exceptionHandler((t, p) -> consumer.accept(p)); }

    public @NotNull S exceptionHandler(@NotNull BiConsumer<? super T, Throwable> consumer) {
        exceptionBiConsumers.add(consumer);
        return (S) this;
    }

    public @NotNull S handler(@NotNull Consumer<? super T> handler) { return handler((t, p) -> handler.accept(p)); }

    private @NotNull S handler(@NotNull BiConsumer<AbstractEventSubscriber<S, E, T>, ? super T> handler) {
        handlerBiConsumers.add(handler);
        return (S) this;
    }

    protected final boolean tryExpire(@NotNull T event, List<BiPredicate<AbstractEventSubscriber<S, E, T>, T>> biCallList, @NotNull SubscriberStage stage) throws EventException {
        if (biCallList == null) {
            return false;
        }

        for (BiPredicate<AbstractEventSubscriber<S, E, T>, T> predicate : biCallList) {
            try {
                if (predicate.test(this, event)) {
                    return true;
                }
            } catch (Exception ex) {
                swallowException(event, ex, stage);
            }
        }

        return false;
    }

    protected final boolean filter(@NotNull T event) throws EventException {
        for (BiPredicate<AbstractEventSubscriber<S, E, T>, T> predicate : filterBiPredicates) {
            try {
                if (!predicate.test(this, event)) {
                    return true;
                }
            } catch (Exception ex) {
                swallowException(event, ex, SubscriberStage.FILTER);
            }
        }
        return false;
    }

    protected final void swallowException(T event, Exception ex, SubscriberStage stage) throws EventException {
        EventException newEx = new EventException(this, baseClass, stage, ex);

        int handled = 0;

        for (BiConsumer<? super T, Throwable> consumer : exceptionBiConsumers) {
            try {
                consumer.accept(event, newEx);
                handled++;
            } catch (Exception ignored) { }
        }

        if (handled == 0) {
            throw newEx;
        }
    }
}
