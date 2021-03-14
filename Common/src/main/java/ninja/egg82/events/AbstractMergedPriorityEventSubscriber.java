package ninja.egg82.events;

import ninja.egg82.events.internal.PriorityHandlerMapping;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

public abstract class AbstractMergedPriorityEventSubscriber<S extends AbstractMergedPriorityEventSubscriber<S, P, E, T>, P, E, T> extends AbstractPriorityEventSubscriber<S, P, T> implements MergedPriorityEventSubscriber<S, P, E, T> {
    protected final ConcurrentMap<Class<? extends E>, PriorityHandlerMapping<P, T>> mappings = new ConcurrentHashMap<>();

    protected AbstractMergedPriorityEventSubscriber(@NotNull Class<T> superclass) {
        super(superclass);
    }

    public @NotNull Class<T> getSuperclass() { return baseClass; }

    @Override
    public void call(@NotNull T event, @NotNull P priority) throws PriorityEventException {
        throw new PriorityEventException(this, baseClass, SubscriberStage.NONE, new UnsupportedOperationException());
    }

    @Override
    public void callMerged(@NotNull Object event, @NotNull P priority) throws PriorityEventException {
        if (cancelState.get()) {
            return;
        }

        if (!this.baseClass.isInstance(event)) {
            return;
        }

        PriorityHandlerMapping<P, T> mapping = mappings.get(event.getClass());
        if (mapping == null) {
            return;
        }

        if (mapping.getPriority() != priority) {
            return;
        }

        calls.incrementAndGet();

        if (expireState.get()) {
            return;
        }

        T obj;
        try {
            obj = mapping.getFunction().apply(event);
        } catch (Exception ex) {
            swallowException(null, ex, SubscriberStage.MAP);
            return;
        }

        syncLock.lock();
        try {
            if (tryExpire(obj, expireBiPredicates.get(TestStage.PRE_FILTER), SubscriberStage.PRE_FILTER_EXPIRE)) {
                expireState.set(true);
                return;
            }

            if (filter(obj)) {
                return;
            }

            if (tryExpire(obj, expireBiPredicates.get(TestStage.POST_FILTER), SubscriberStage.POST_FILTER_EXPIRE)) {
                expireState.set(true);
                return;
            }

            for (BiConsumer<AbstractPriorityEventSubscriber<S, P, T>, ? super T> consumer : handlerBiConsumers) {
                try {
                    consumer.accept(this, obj);
                } catch (Exception ex) {
                    swallowException(obj, ex, SubscriberStage.HANDLE);
                }
            }

            if (tryExpire(obj, expireBiPredicates.get(TestStage.POST_HANDLE), SubscriberStage.POST_HANDLE_EXPIRE)) {
                expireState.set(true);
            }
        } finally {
            syncLock.unlock();
        }
    }
}
