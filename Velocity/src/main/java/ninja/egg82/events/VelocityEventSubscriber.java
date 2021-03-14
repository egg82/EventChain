package ninja.egg82.events;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;

public class VelocityEventSubscriber<T> extends AbstractPriorityEventSubscriber<VelocityEventSubscriber<T>, PostOrder, T> {
    private final Object plugin;
    private final ProxyServer proxy;

    private final EventHandler<T> handler;

    public VelocityEventSubscriber(@NotNull Object plugin, @NotNull ProxyServer proxy, @NotNull Class<T> event, @NotNull PostOrder priority) {
        super(event);

        this.plugin = plugin;
        this.proxy = proxy;

        handler = e -> {
            if (!baseClass.isInstance(e)) {
                return;
            }

            try {
                call(e, priority);
            } catch (PriorityEventException ex) {
                throw new RuntimeException("Could not call event subscriber.", ex);
            }
        };

        proxy.getEventManager().register(plugin, event, priority, handler);
    }

    @Override
    public void cancel() {
        super.cancel();
        proxy.getEventManager().unregister(plugin, handler);
    }
}
