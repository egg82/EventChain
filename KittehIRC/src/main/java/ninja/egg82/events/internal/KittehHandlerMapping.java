package ninja.egg82.events.internal;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.event.helper.ClientEvent;

public class KittehHandlerMapping<E extends ClientEvent, T> extends AbstractHandlerMapping<E, T> {
    public KittehHandlerMapping(@NotNull Function<E, T> function) {
        super(function);
    }
}
