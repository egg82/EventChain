package ninja.egg82.events;

/**
 * Test states are used with {@link PriorityEventSubscriber} expiration
 * methods to test for expiration predicates at various
 * stages of event handling.
 */
public enum TestStage {
    /**
     * First, directly before event filtering
     */
    PRE_FILTER,

    /**
     * After event filtering and directly before event handling
     */
    POST_FILTER,

    /**
     * Last, directly after event handling
     */
    POST_HANDLE
}
