package ninja.egg82.events;

/**
 * The stage at which an {@link EventException}
 * was thrown from a {@link PriorityEventSubscriber}.
 */
public enum SubscriberStage {
    /**
     * When using {@link MergedEventSubscriber},
     * the value mapping stage.
     */
    MAP,

    /**
     * The pre-filter expire test stage.
     */
    PRE_FILTER_EXPIRE,

    /**
     * The filter stage.
     */
    FILTER,

    /**
     * The post-filter expire test stage.
     */
    POST_FILTER_EXPIRE,

    /**
     * The handle stage.
     */
    HANDLE,

    /**
     * The post-handle expire test stage.
     */
    POST_HANDLE_EXPIRE
}
