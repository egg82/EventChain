package ninja.egg82.events;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import ninja.egg82.events.internal.VelocityHandlerMapping;
import org.jetbrains.annotations.NotNull;

public class VelocityMergedEventSubscriber<E1, T> extends AbstractMergedPriorityEventSubscriber<VelocityMergedEventSubscriber<E1, T>, PostOrder, E1, T> {
    private final Object plugin;
    private final ProxyServer proxy;

    private final List<EventHandler<E1>> handlers = new CopyOnWriteArrayList<>();

    public VelocityMergedEventSubscriber(@NotNull Object plugin, @NotNull ProxyServer proxy, @NotNull Class<T> superclass) {
        super(superclass);

        this.plugin = plugin;
        this.proxy = proxy;
    }

    @Override
    public @NotNull VelocityMergedEventSubscriber<E1, T> bind(@NotNull Class<E1> event, @NotNull PostOrder priority, @NotNull Function<E1, T> function) {
        mappings.put(event, new VelocityHandlerMapping<>(priority, function));

        EventHandler<E1> handler = e -> {
            try {
                callMerged(e, priority);
            } catch (PriorityEventException ex) {
                throw new RuntimeException("Could not call event subscriber.", ex);
            }
        };
        handlers.add(handler);

        proxy.getEventManager().register(plugin, event, priority, handler);

        return this;
    }

    @Override
    public void cancel() {
        super.cancel();

        for (EventHandler<?> handler : handlers) {
            proxy.getEventManager().unregister(plugin, handler);
        }
        handlers.clear();
    }
}
