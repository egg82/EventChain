package ninja.egg82.events.internal;

import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class JDAHandlerMapping<E extends GenericEvent, T> extends AbstractHandlerMapping<E, T> {
    public JDAHandlerMapping(@NotNull Function<E, T> function) {
        super(function);
    }
}
