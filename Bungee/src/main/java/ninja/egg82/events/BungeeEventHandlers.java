package ninja.egg82.events;

import net.md_5.bungee.api.plugin.Cancellable;

import java.util.function.Consumer;

/**
 * This class stolen from Luck's Helper @ https://github.com/lucko/helper/blob/master/helper/src/main/java/me/lucko/helper/event/filter/EventHandlers.java
 */
public class BungeeEventHandlers {
    private BungeeEventHandlers() { }

    /**
     * Returns a consumer which cancels the event
     *
     * @param <T> the event type
     *
     * @return a consumer which cancels the event
     */
    public static <T extends Cancellable> Consumer<T> cancel() { return e -> e.setCancelled(true); }

    /**
     * Returns a consumer which un-cancels the event
     *
     * @param <T> the event type
     *
     * @return a consumer which un-cancels the event
     */
    public static <T extends Cancellable> Consumer<T> uncancel() { return e -> e.setCancelled(false); }
}
