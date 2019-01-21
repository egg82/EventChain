package ninja.egg82.events;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

public abstract class MergedEventSubscriber<T> {
    private Class<T> commonClass;
    protected long callCount = 0L;
    protected volatile boolean cancelled = false;
    protected boolean expired = false;

    protected ConcurrentMap<TestStage, List<Predicate<T>>> expirePredicates = new ConcurrentHashMap<>();
    protected ConcurrentMap<TestStage, List<BiPredicate<? extends MergedEventSubscriber<T>, T>>> expireBiPredicates = new ConcurrentHashMap<>();

    private List<Predicate<T>> filterPredicates = new CopyOnWriteArrayList<>();
    private List<BiPredicate<? extends MergedEventSubscriber<T>, T>> filterBiPredicates = new CopyOnWriteArrayList<>();

    private List<Consumer<Throwable>> exceptionConsumers = new CopyOnWriteArrayList<>();
    private List<BiConsumer<? super T, Throwable>> exceptionBiConsumers = new CopyOnWriteArrayList<>();

    protected List<Consumer<? super T>> handlerConsumers = new CopyOnWriteArrayList<>();
    protected List<BiConsumer<? extends MergedEventSubscriber<T>, ? super T>> handlerBiConsumers = new CopyOnWriteArrayList<>();

    protected MergedEventSubscriber(Class<T> commonClass) {
        if (commonClass == null) {
            throw new IllegalArgumentException("commonClass cannot be null.");
        }

        this.commonClass = commonClass;
    }

    protected Class<T> getCommonClass() { return commonClass; }

    public long getCallCount() { return callCount; }

    public boolean isCancelled() { return cancelled; }

    public boolean isExpired() { return expired; }

    public MergedEventSubscriber<T> expireAfter(long duration, TimeUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("unit cannot be null.");
        }
        if (duration <= 0L) {
            throw new IllegalArgumentException("duration cannot be <= 0.");
        }

        long expireTime = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
        return expireIfBi((h, e) -> System.currentTimeMillis() > expireTime, TestStage.PRE_FILTER);
    }

    public MergedEventSubscriber<T> expireAfterCalls(long calls) {
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
            } catch (Exception ex) {
                swallowException(event, ex);
            }
        }
        for (BiConsumer<? extends MergedEventSubscriber<T>, ? super T> consumer : handlerBiConsumers) {
            BiConsumer<MergedEventSubscriber<T>, ? super T> c = (BiConsumer<MergedEventSubscriber<T>, ? super T>) consumer;
            try {
                c.accept(this, event);
            } catch (Exception ex) {
                swallowException(event, ex);
            }
        }

        expire(event, expirePredicates.get(TestStage.POST_HANDLE), expireBiPredicates.get(TestStage.POST_HANDLE));
    }

    public void cancel() {
        cancelled = true;
    }

    protected boolean expire(T event, List<Predicate<T>> callList, List<BiPredicate<? extends MergedEventSubscriber<T>, T>> biCallList) throws Exception {
        if (callList != null) {
            for (Predicate<T> predicate : callList) {
                boolean test;

                try {
                    test = predicate.test(event);
                } catch (Exception ex) {
                    swallowException(event, ex);
                    continue;
                }

                if (test) {
                    expired = true;
                    return true;
                }
            }
        }
        if (biCallList != null) {
            for (BiPredicate<? extends MergedEventSubscriber<T>, T> predicate : biCallList) {
                BiPredicate<MergedEventSubscriber<T>, T> p = (BiPredicate<MergedEventSubscriber<T>, T>) predicate;
                boolean test;

                try {
                    test = p.test(this, event);
                } catch (Exception ex) {
                    swallowException(event, ex);
                    continue;
                }

                if (test) {
                    expired = true;
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean filter(T event) throws Exception {
        for (Predicate<T> predicate : filterPredicates) {
            boolean test;

            try {
                test = predicate.test(event);
            } catch (Exception ex) {
                swallowException(event, ex);
                continue;
            }

            if (!test) {
                return true;
            }
        }
        for (BiPredicate<? extends MergedEventSubscriber<T>, T> predicate : filterBiPredicates) {
            BiPredicate<MergedEventSubscriber<T>, T> p = (BiPredicate<MergedEventSubscriber<T>, T>) predicate;

            boolean test;

            try {
                test = p.test(this, event);
            } catch (Exception ex) {
                swallowException(event, ex);
                continue;
            }

            if (!test) {
                return true;
            }
        }
        return false;
    }

    protected void swallowException(T event, Exception ex) throws Exception {
        int handled = 0;

        for (Consumer<Throwable> consumer : exceptionConsumers) {
            consumer.accept(ex);
            handled++;
        }
        for (BiConsumer<? super T, Throwable> consumer : exceptionBiConsumers) {
            consumer.accept(event, ex);
            handled++;
        }

        if (handled == 0) {
            throw ex;
        }
    }

    public MergedEventSubscriber<T> expireIf(Predicate<T> predicate) { return expireIf(predicate, TestStage.PRE_FILTER, TestStage.POST_HANDLE); }

    public MergedEventSubscriber<T> expireIf(Predicate<T> predicate, TestStage... stages) {
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

    protected MergedEventSubscriber<T> expireIfBi(BiPredicate<? extends MergedEventSubscriber<T>, T> predicate) { return expireIfBi(predicate, TestStage.PRE_FILTER, TestStage.POST_HANDLE); }

    protected MergedEventSubscriber<T> expireIfBi(BiPredicate<? extends MergedEventSubscriber<T>, T> predicate, TestStage... stages) {
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

    public MergedEventSubscriber<T> filter(Predicate<T> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("predicate cannot be null.");
        }

        filterPredicates.add(predicate);
        return this;
    }

    protected MergedEventSubscriber<T> filterBi(BiPredicate<? extends MergedEventSubscriber<T>, T> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("predicate cannot be null.");
        }

        filterBiPredicates.add(predicate);
        return this;
    }

    public MergedEventSubscriber<T> exceptionHandler(Consumer<Throwable> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("consumer cannot be null.");
        }

        exceptionConsumers.add(consumer);
        return this;
    }

    public MergedEventSubscriber<T> exceptionHandler(BiConsumer<? super T, Throwable> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("consumer cannot be null.");
        }

        exceptionBiConsumers.add(consumer);
        return this;
    }

    public MergedEventSubscriber<T> handler(Consumer<? super T> handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }

        handlerConsumers.add(handler);
        return this;
    }

    protected MergedEventSubscriber<T> handlerBi(BiConsumer<? extends MergedEventSubscriber<T>, ? super T> handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }

        handlerBiConsumers.add(handler);
        return this;
    }
}
