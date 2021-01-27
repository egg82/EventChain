package ninja.egg82.events;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.proxy.ProxyServer;

public class VelocityEvents {
    private VelocityEvents() { }

    public static <T> VelocityEventSubscriber<T> subscribe(Object plugin, ProxyServer proxy, Class<T> event, PostOrder order) { return new VelocityEventSubscriber<>(plugin, proxy, event, order); }

    public static void call(ProxyServer proxy, Object event) { proxy.getEventManager().fireAndForget(event); }

    public static void callAsync(Object plugin, ProxyServer proxy, Object event) { proxy.getScheduler().buildTask(plugin, () -> call(proxy, event)); }

    public static <T> VelocityMergedEventSubscriber<T> merge(Object plugin, ProxyServer proxy, Class<T> superclass) { return new VelocityMergedEventSubscriber<>(plugin, proxy, superclass); }

    public static <T> VelocityMergedEventSubscriber<T> merge(Object plugin, ProxyServer proxy, Class<T> superclass, Class<? extends T>... events) { return merge(plugin, proxy, superclass, PostOrder.NORMAL, events); }

    public static <T> VelocityMergedEventSubscriber<T> merge(Object plugin, ProxyServer proxy, Class<T> superclass, PostOrder order, Class<? extends T>... events) {
        VelocityMergedEventSubscriber<T> subscriber = new VelocityMergedEventSubscriber<>(plugin, proxy, superclass);
        for (Class<? extends T> clazz : events) {
            subscriber.bind(clazz, order, e -> e);
        }
        return subscriber;
    }
}
