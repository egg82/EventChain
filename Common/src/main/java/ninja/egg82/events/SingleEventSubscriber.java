package ninja.egg82.events;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class SingleEventSubscriber<T> {
    private Class<T> event;
    private long callCount = 0L;
    private volatile boolean cancelled = false;
    private boolean expired = false;

    private ConcurrentMap<TestStage, List<Predicate<T>>> expirePredicates = new ConcurrentHashMap<>();
    private ConcurrentMap<TestStage, List<BiPredicate<? extends SingleEventSubscriber<T>, T>>> expireBiPredicates = new ConcurrentHashMap<>();

    private List<Predicate<T>> filterPredicates = new CopyOnWriteArrayList<>();
    private List<BiPredicate<? extends SingleEventSubscriber<T>, T>> filterBiPredicates = new CopyOnWriteArrayList<>();

    private List<Consumer<Throwable>> exceptionConsumers = new CopyOnWriteArrayList<>();
    private List<BiConsumer<? super T, Throwable>> exceptionBiConsumers = new CopyOnWriteArrayList<>();

    private List<Consumer<? super T>> handlerConsumers = new CopyOnWriteArrayList<>();
    private List<BiConsumer<? extends SingleEventSubscriber<T>, ? super T>> handlerBiConsumers = new CopyOnWriteArrayList<>();

    protected SingleEventSubscriber(Class<T> event) {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }

        this.event = event;
    }

    protected Class<T> getEventClass() { return event; }

    public long getCallCount() { return callCount; }

    public boolean isCancelled() { return cancelled; }

    public boolean isExpired() { return expired; }

    public SingleEventSubscriber<T> expireAfter(long duration, TimeUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("unit cannot be null.");
        }
        if (duration <= 0L) {
            throw new IllegalArgumentException("duration cannot be <= 0.");
        }

        long expireTime = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
        return expireIfBi((h, e) -> System.currentTimeMillis() > expireTime, TestStage.PRE_FILTER);
    }

    public SingleEventSubscriber<T> expireAfterCalls(long calls) {
        if (calls <= 0L) {
            throw new IllegalArgumentException("calls cannot be <= 0.");
        }

        return expireIfBi((h, e) -> getCallCount() >= calls, TestStage.PRE_FILTER, TestStage.POST_HANDLE);
    }

    public synchronized void call(T event) throws Exception {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }

        if (cancelled) {
            return;
        }

        callCount++;

        if (expired) {
            return;
        }

        if (expire(event, expirePredicates.get(TestStage.PRE_FILTER), expireBiPredicates.get(TestStage.PRE_FILTER))) {
            return;
        }

        if (filter(event)) {
            return;
        }

        if (expire(event, expirePredicates.get(TestStage.POST_FILTER), expireBiPredicates.get(TestStage.POST_FILTER))) {
            return;
        }

        for (Consumer<? super T> consumer : handlerConsumers) {
            try {
                consumer.accept(event);
            } catch (ClassCastException ignored) {

            } catch (Exception ex) {
                swallowException(event, ex);
            }
        }
        for (BiConsumer<? extends SingleEventSubscriber<T>, ? super T> consumer : handlerBiConsumers) {
            BiConsumer<SingleEventSubscriber<T>, ? super T> c = (BiConsumer<SingleEventSubscriber<T>, ? super T>) consumer;
            try {
                c.accept(this, event);
            } catch (ClassCastException ignored) {

            } catch (Exception ex) {
                swallowException(event, ex);
            }
        }

        expire(event, expirePredicates.get(TestStage.POST_HANDLE), expireBiPredicates.get(TestStage.POST_HANDLE));
    }

    public void cancel() {
        cancelled = true;
    }

    private boolean expire(T event, List<Predicate<T>> callList, List<BiPredicate<? extends SingleEventSubscriber<T>, T>> biCallList) {
        if (callList != null) {
            for (Predicate<T> predicate : callList) {
                if (predicate.test(event)) {
                    expired = true;
                    return true;
                }
            }
        }
        if (biCallList != null) {
            for (BiPredicate<? extends SingleEventSubscriber<T>, T> predicate : biCallList) {
                BiPredicate<SingleEventSubscriber<T>, T> p = (BiPredicate<SingleEventSubscriber<T>, T>) predicate;
                if (p.test(this, event)) {
                    expired = true;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean filter(T event) {
        for (Predicate<T> predicate : filterPredicates) {
            if (!predicate.test(event)) {
                return true;
            }
        }
        for (BiPredicate<? extends SingleEventSubscriber<T>, T> predicate : filterBiPredicates) {
            BiPredicate<SingleEventSubscriber<T>, T> p = (BiPredicate<SingleEventSubscriber<T>, T>) predicate;
            if (!p.test(this, event)) {
                return true;
            }
        }
        return false;
    }

    private void swallowException(T event, Exception ex) throws Exception {
        int handled = 0;

        for (Consumer<Throwable> consumer : exceptionConsumers) {
            consumer.accept(ex);
            handled++;
        }
        for (BiConsumer<? super T, Throwable> consumer : exceptionBiConsumers) {
            try {
                consumer.accept(event, ex);
            } catch (ClassCastException ignored) {}
            handled++;
        }

        if (handled == 0) {
            throw ex;
        }
    }

    public SingleEventSubscriber<T> expireIf(Predicate<T> predicate) { return expireIf(predicate, TestStage.PRE_FILTER, TestStage.POST_HANDLE); }

    public SingleEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) {
        if (predicate == null) {
            throw new IllegalArgumentException("predicate cannot be null.");
        }
        if (stages == null) {
            throw new IllegalArgumentException("stages cannot be null.");
        }

        for (TestStage stage : stages) {
            expirePredicates.computeIfAbsent(stage, k -> new CopyOnWriteArrayList<>()).add(predicate);
        }
        return this;
    }

    protected SingleEventSubscriber<T> expireIfBi(BiPredicate<? extends SingleEventSubscriber<T>, T> predicate) { return expireIfBi(predicate, TestStage.PRE_FILTER, TestStage.POST_HANDLE); }

    protected SingleEventSubscriber<T> expireIfBi(BiPredicate<? extends SingleEventSubscriber<T>, T> predicate, TestStage... stages) {
        if (predicate == null) {
            throw new IllegalArgumentException("predicate cannot be null.");
        }
        if (stages == null) {
            throw new IllegalArgumentException("stages cannot be null.");
        }

        for (TestStage stage : stages) {
            expireBiPredicates.computeIfAbsent(stage, k -> new CopyOnWriteArrayList<>()).add(predicate);
        }
        return this;
    }

    public SingleEventSubscriber<T> filter(Predicate<T> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("predicate cannot be null.");
        }

        filterPredicates.add(predicate);
        return this;
    }

    protected SingleEventSubscriber<T> filterBi(BiPredicate<? extends SingleEventSubscriber<T>, T> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("predicate cannot be null.");
        }

        filterBiPredicates.add(predicate);
        return this;
    }

    public SingleEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("consumer cannot be null.");
        }

        exceptionConsumers.add(consumer);
        return this;
    }

    public SingleEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("consumer cannot be null.");
        }

        exceptionBiConsumers.add(consumer);
        return this;
    }

    public SingleEventSubscriber<T> handler(Consumer<? super T> handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }

        handlerConsumers.add(handler);
        return this;
    }

    protected SingleEventSubscriber<T> handlerBi(BiConsumer<? extends SingleEventSubscriber<T>, ? super T> handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }

        handlerBiConsumers.add(handler);
        return this;
    }
}
