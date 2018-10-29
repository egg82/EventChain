package ninja.egg82.events.internal;

import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import ninja.egg82.events.BungeeEventSubscriber;
import ninja.egg82.events.MergedBungeeEventSubscriber;

public class BungeeAllEventsListener<E extends Event> implements Listener {
    private BungeeEventSubscriber<E> singleEventSubscriber;
    private MergedBungeeEventSubscriber<?> mergedEventSubscriber;

    public BungeeAllEventsListener(BungeeEventSubscriber<E> eventSubscriber) {
        this.singleEventSubscriber = eventSubscriber;
        this.mergedEventSubscriber = null;
    }

    public <T> BungeeAllEventsListener(MergedBungeeEventSubscriber<T> eventSubscriber) {
        this.singleEventSubscriber = null;
        this.mergedEventSubscriber = eventSubscriber;
    }

    // generic events
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncHighest(AsyncEvent<?> e) { onAnyEvent(EventPriority.HIGHEST, e, e.getClass()); }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncHigh(AsyncEvent<?> e) { onAnyEvent(EventPriority.HIGH, e, e.getClass()); }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAsync(AsyncEvent<?> e) { onAnyEvent(EventPriority.NORMAL, e, e.getClass()); }

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncLow(AsyncEvent<?> e) { onAnyEvent(EventPriority.LOW, e, e.getClass()); }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncLowest(AsyncEvent<?> e) { onAnyEvent(EventPriority.LOWEST, e, e.getClass()); }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGenericHighest(Event e) { onAnyEvent(EventPriority.HIGHEST, e, e.getClass()); }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGenericHigh(Event e) { onAnyEvent(EventPriority.HIGH, e, e.getClass()); }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onGeneric(Event e) { onAnyEvent(EventPriority.NORMAL, e, e.getClass()); }

    @EventHandler(priority = EventPriority.LOW)
    public void onGenericLow(Event e) { onAnyEvent(EventPriority.LOW, e, e.getClass()); }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGenericLowest(Event e) { onAnyEvent(EventPriority.LOWEST, e, e.getClass()); }

    /*
    // player events
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLoginHighest(PreLoginEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPreLoginHigh(PreLoginEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPreLogin(PreLoginEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPreLoginLow(PreLoginEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLoginLowest(PreLoginEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLoginHighest(LoginEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLoginHigh(LoginEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLogin(LoginEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLoginLow(LoginEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoginLowest(LoginEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLoginHighest(PostLoginEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPostLoginHigh(PostLoginEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPostLogin(PostLoginEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPostLoginLow(PostLoginEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLoginLowest(PostLoginEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerHandshakeHighest(PlayerHandshakeEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerHandshakeHigh(PlayerHandshakeEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerHandshake(PlayerHandshakeEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerHandshakeLow(PlayerHandshakeEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerHandshakeLowest(PlayerHandshakeEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnectHighest(PlayerDisconnectEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDisconnectHigh(PlayerDisconnectEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDisconnectLow(PlayerDisconnectEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDisconnectLowest(PlayerDisconnectEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnectHighest(ServerConnectEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerConnectHigh(ServerConnectEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerConnect(ServerConnectEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onServerConnectLow(ServerConnectEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerConnectLowest(ServerConnectEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerDisconnectHighest(ServerDisconnectEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerDisconnectHigh(ServerDisconnectEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerDisconnect(ServerDisconnectEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onServerDisconnectLow(ServerDisconnectEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerDisconnectLowest(ServerDisconnectEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerKickHighest(ServerKickEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerKickHigh(ServerKickEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerKick(ServerKickEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onServerKickLow(ServerKickEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerKickLowest(ServerKickEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerSwitchHighest(ServerSwitchEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerSwitchHigh(ServerSwitchEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerSwitch(ServerSwitchEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onServerSwitchLow(ServerSwitchEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerSwitchLowest(ServerSwitchEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    // chat events
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatHighest(ChatEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChatHigh(ChatEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(ChatEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChatLow(ChatEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatLowest(ChatEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPermissionCheckHighest(PermissionCheckEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPermissionCheckHigh(PermissionCheckEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPermissionCheck(PermissionCheckEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPermissionCheckLow(PermissionCheckEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPermissionCheckLowest(PermissionCheckEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabCompleteHighest(TabCompleteEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTabCompleteHigh(TabCompleteEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTabComplete(TabCompleteEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onTabCompleteLow(TabCompleteEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTabCompleteLowest(TabCompleteEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabCompleteResponseHighest(TabCompleteResponseEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTabCompleteResponseHigh(TabCompleteResponseEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTabCompleteResponse(TabCompleteResponseEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onTabCompleteResponseLow(TabCompleteResponseEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTabCompleteResponseLowest(TabCompleteResponseEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    // plugin events
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginMessageHighest(PluginMessageEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPluginMessageHigh(PluginMessageEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPluginMessage(PluginMessageEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPluginMessageLow(PluginMessageEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPluginMessageLowest(PluginMessageEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    // proxy events
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyPingHighest(ProxyPingEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProxyPingHigh(ProxyPingEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProxyPing(ProxyPingEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onProxyPingLow(ProxyPingEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProxyPingLowest(ProxyPingEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyReloadHighest(ProxyReloadEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProxyReloadHigh(ProxyReloadEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProxyReload(ProxyReloadEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onProxyReloadLow(ProxyReloadEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProxyReloadLowest(ProxyReloadEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    // server events
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnectedHighest(ServerConnectedEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerConnectedHigh(ServerConnectedEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerConnected(ServerConnectedEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onServerConnectedLow(ServerConnectedEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerConnectedLowest(ServerConnectedEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTargetedHighest(TargetedEvent e) {
        onAnyEvent(EventPriority.HIGHEST, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTargetedHigh(TargetedEvent e) {
        onAnyEvent(EventPriority.HIGH, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTargeted(TargetedEvent e) {
        onAnyEvent(EventPriority.NORMAL, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onTargetedLow(TargetedEvent e) {
        onAnyEvent(EventPriority.LOW, e, e.getClass());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTargetedLowest(TargetedEvent e) {
        onAnyEvent(EventPriority.LOWEST, e, e.getClass());
    }
    */

    private <S extends Event> void onAnyEvent(byte priority, S event, Class<? extends Event> clazz) {
        if (singleEventSubscriber != null) {
            if (clazz.equals(singleEventSubscriber.getEventClass())) {
                try {
                    singleEventSubscriber.call((E) event, priority);
                } catch (RuntimeException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new RuntimeException("Could not call event handler.", ex);
                }
            }
            return;
        }
        if (mergedEventSubscriber != null && mergedEventSubscriber.getEventClasses().contains(clazz)) {
            try {
                mergedEventSubscriber.call(event, priority);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException("Could not call event handler.", ex);
            }
        }
    }
}
