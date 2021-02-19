package ninja.egg82.events;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a basic priority-driven event subscriber.
 *
 * <p>This interface only exists to give basic
 * functionality to the different event subscribers
 * types. It should not be directly implemented.</p>
 *
 * @see MergedPriorityEventSubscriber
 * @see MergedEventSubscriber
 * @see SinglePriorityEventSubscriber
 * @see SingleEventSubscriber
 *
 * @param <S> the class type of the subscriber
 * @param <P> the class type of the priority system
 * @param <T> means different things based on the implementation
 */
public interface PriorityEventSubscriber<S extends PriorityEventSubscriber<S, P, T>, P, T> {
    /**
     * Gets the base class of the subscriber.
     *
     * <p>With a {@link MergedPriorityEventSubscriber} or
     * {@link MergedEventSubscriber}, this is the superclass
     * that all events share in common.</p>
     *
     * <p>With a {@link SinglePriorityEventSubscriber} or
     * {@link SingleEventSubscriber}, this is the event
     * class itself.</p>
     *
     * @return the base class of the subscriber
     */
    @NotNull Class<T> getBaseClass();

    /**
     * Gets an {@link AtomicBoolean} holding the cancellation state of the subscriber
     *
     * @return the cancellation
     */
    @NotNull AtomicBoolean cancellationState();

    /**
     * Returns true if the subscriber is currently cancelled.
     *
     * @return if the subscriber is cancelled
     */
    default boolean isCancelled() { return cancellationState().get(); }

    /**
     * Returns true if the subscriber is not currently cancelled.
     *
     * @return if the subscriber is not cancelled
     */
    default boolean isNotCancelled() { return !cancellationState().get(); }

    /**
     * Gets an {@link AtomicBoolean} holding the expiration state of the subscriber
     *
     * @return the expiration
     */
    @NotNull AtomicBoolean expirationState();

    /**
     * Cancels the subscriber and sets the cancellation
     * state to true.
     */
    void cancel();

    /**
     * Returns true if the subscriber is currently expired.
     *
     * @return if the subscriber is expired
     */
    default boolean isExpired() { return expirationState().get(); }

    /**
     * Returns true if the subscriber is not currently expired.
     *
     * @return if the subscriber is not expired
     */
    default boolean isNotExpired() { return !expirationState().get(); }

    /**
     * Sets the expiration state of the subscriber.
     *
     * @param expired the new state
     * @return the previous state
     */
    default boolean setExpired(boolean expired) { return expirationState().getAndSet(expired); }

    /**
     * Gets an {@link AtomicLong} holding the current call count of the subscriber
     *
     * @return the call count
     */
    @NotNull AtomicLong callCount();

    /**
     * Returns the current call count of the subscriber.
     *
     * @return the current call count of the subscriber
     */
    default long getCallCount() { return callCount().get(); }

    /**
     * Runs an event through this subscriber chain.
     *
     * @param event The event to call
     * @param priority The event priority
     * @throws PriorityEventException if an exception was thrown in the chain
     * @throws NullPointerException if the {@code event} or {@code priority} is null
     */
    void call(@NotNull T event, @NotNull P priority) throws PriorityEventException;

    /**
     * Expires the subscriber after a set amount of time.
     *
     * <p>This uses the {@link TestStage#PRE_FILTER} and {@link TestStage#POST_HANDLE} test stages.</p>
     *
     * @param duration the length of time after the subscriber is created that it should be automatically expired
     * @param unit the unit that {@code duration} is expressed in
     * @return this {@link PriorityEventSubscriber} instance (for chaining)
     * @throws IllegalArgumentException if {@code duration} is negative
     * @throws NullPointerException if the {@code unit} is null
     */
    @NotNull S expireAfter(long duration, @NotNull TimeUnit unit);

    /**
     * Expires the subscriber after a set number of calls.
     *
     * <p>This uses the {@link TestStage#PRE_FILTER} and {@link TestStage#POST_HANDLE} test stages.</p>
     *
     * @param calls the number of calls after the subscriber is created that it should be automatically expired
     * @return this {@link PriorityEventSubscriber} instance (for chaining)
     * @throws IllegalArgumentException if {@code calls} is negative
     */
    @NotNull S expireAfterCalls(long calls);

    /**
     * Expires the subscriber after a {@link Predicate} returns true.
     *
     * <p>This defaults to the {@link TestStage#PRE_FILTER} and {@link TestStage#POST_HANDLE} test stages.</p>
     *
     * @param predicate the predicate that, after it returns true, automatically expires this subscriber
     * @return this {@link PriorityEventSubscriber} instance (for chaining)
     * @throws NullPointerException if the {@code predicate} is null
     */
    default @NotNull S expireIf(@NotNull Predicate<T> predicate) { return expireIf(predicate, TestStage.PRE_FILTER, TestStage.POST_HANDLE); }

    /**
     * Expires the subscriber after a {@link Predicate} returns true.
     *
     * @param predicate the predicate that, after it returns true, automatically expires this subscriber
     * @param stages the {@link TestStage}s to use for this predicate
     * @return this {@link PriorityEventSubscriber} instance (for chaining)
     * @throws NullPointerException if the {@code predicate} or {@code stages} are null
     */
    default @NotNull S expireIf(@NotNull Predicate<T> predicate, @NotNull Collection<TestStage> stages) { return expireIf(predicate, stages.toArray(new TestStage[0])); }

    /**
     * Expires the subscriber after a {@link Predicate} returns true.
     *
     * @param predicate the predicate that, after it returns true, automatically expires this subscriber
     * @param stages the {@link TestStage}s to use for this predicate
     * @return this {@link PriorityEventSubscriber} instance (for chaining)
     * @throws NullPointerException if the {@code predicate} or {@code stages} are null
     */
    @NotNull S expireIf(@NotNull Predicate<T> predicate, @NotNull TestStage... stages);

    /**
     * Filters the subscriber so the event will be handled only if this predicate returns true.
     *
     * @param predicate the predicate that, after it returns true, automatically expires this subscriber
     * @return this {@link PriorityEventSubscriber} instance (for chaining)
     * @throws NullPointerException if the {@code predicate} or {@code stages} are null
     */
    @NotNull S filter(@NotNull Predicate<T> predicate);

    /**
     * Handles any exceptions thrown from this subscriber.
     *
     * <p>This consumer takes the exception itself.</p>
     *
     * @param consumer the consumer that will handle the exception
     * @return this {@link PriorityEventSubscriber} instance (for chaining)
     * @throws NullPointerException if the {@code consumer} is null
     */
    @NotNull S exceptionHandler(@NotNull Consumer<Throwable> consumer);

    /**
     * Handles any exceptions thrown from this subscriber.
     *
     * <p>This consumer takes the exception and the event.</p>
     *
     * @param consumer the consumer that will handle the exception
     * @return this {@link PriorityEventSubscriber} instance (for chaining)
     * @throws NullPointerException if the {@code consumer} is null
     */
    @NotNull S exceptionHandler(@NotNull BiConsumer<? super T, Throwable> consumer);

    /**
     * Handles the event from this subscriber.
     *
     * <p>This consumer takes the event itself.</p>
     *
     * @param handler the consumer that will handle the final event
     * @return this {@link PriorityEventSubscriber} instance (for chaining)
     * @throws NullPointerException if the {@code handler} is null
     */
    @NotNull S handler(@NotNull Consumer<? super T> handler);
}
