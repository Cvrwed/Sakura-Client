package cc.unknown.script.api.wrapper.impl.event.impl.other;

import cc.unknown.event.impl.other.ServerKickEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptServerKickEvent extends ScriptEvent<ServerKickEvent> {

    public ScriptServerKickEvent(final ServerKickEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public String[] getReason() {
        return wrapped.getMessage().toArray(new String[0]);
    }

    @Override
    public String getHandlerName() {
        return "onServerKick";
    }
}
