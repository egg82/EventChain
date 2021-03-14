package ninja.egg82.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.function.Predicate;

/**
 * This class stolen from Luck's Helper @ https://github.com/lucko/helper/blob/master/helper/src/main/java/me/lucko/helper/event/filter/EventFilters.java
 */
public class BukkitEventFilters {
    private BukkitEventFilters() { }

    /**
     * Returns a predicate which only returns true if the event isn't cancelled
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the event isn't cancelled
     */
    public static <T extends Cancellable> Predicate<T> ignoreCancelled() { return e -> !e.isCancelled(); }

    /**
     * Returns a predicate which only returns true if the event is cancelled
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the event is cancelled
     */
    public static <T extends Cancellable> Predicate<T> ignoreNotCancelled() { return Cancellable::isCancelled; }

    /**
     * Returns a predicate which only returns true if the login is allowed
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the login is allowed
     */
    public static <T extends PlayerLoginEvent> Predicate<T> ignoreDisallowedLogin() { return e -> e.getResult() == PlayerLoginEvent.Result.ALLOWED; }

    /**
     * Returns a predicate which only returns true if the login is allowed
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the login is allowed
     */
    public static <T extends AsyncPlayerPreLoginEvent> Predicate<T> ignoreDisallowedPreLogin() { return e -> e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED; }

    /**
     * Returns a predicate which only returns true if the player has moved over a block
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the player has moved over a block
     */
    public static <T extends PlayerMoveEvent> Predicate<T> ignoreSameBlock() {
        return e -> e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom()
                .getBlockZ() != e.getTo().getBlockZ() || e.getFrom().getBlockY() != e.getTo().getBlockY() || !e.getFrom().getWorld().equals(e.getTo().getWorld());
    }

    /**
     * Returns a predicate which only returns true if the player has moved over a block, not including movement
     * directly up and down. (so jumping wouldn't return true)
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the player has moved across a block border
     */
    public static <T extends PlayerMoveEvent> Predicate<T> ignoreSameBlockAndY() {
        return e -> e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom()
                .getBlockZ() != e.getTo().getBlockZ() || !e.getFrom().getWorld().equals(e.getTo().getWorld());
    }

    /**
     * Returns a predicate which only returns true if the player has moved over a chunk border
     *
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the player has moved over a chunk border
     */
    public static <T extends PlayerMoveEvent> Predicate<T> ignoreSameChunk() {
        return e -> (e.getFrom().getBlockX() >> 4) != (e.getTo().getBlockX() >> 4) || (e.getFrom()
                .getBlockZ() >> 4) != (e.getTo().getBlockZ() >> 4) || !e.getFrom().getWorld().equals(e.getTo().getWorld());
    }

    /**
     * Returns a predicate which only returns true if the player has the given permission
     *
     * @param permission the permission
     * @param <T> the event type
     *
     * @return a predicate which only returns true if the player has the given permission
     */
    public static <T extends PlayerEvent> Predicate<T> playerHasPermission(String permission) { return e -> e.getPlayer().hasPermission(permission); }
}
