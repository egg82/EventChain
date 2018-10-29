package ninja.egg82.events;

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
