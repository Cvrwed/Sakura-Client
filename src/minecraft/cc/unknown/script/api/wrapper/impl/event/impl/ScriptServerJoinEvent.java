package cc.unknown.script.api.wrapper.impl.event.impl;

import cc.unknown.event.impl.other.ServerJoinEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptServerJoinEvent extends ScriptEvent<ServerJoinEvent> {

    public ScriptServerJoinEvent(final ServerJoinEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public String getIp() {
        return wrapped.getIp();
    }

    public int getPort() {
        return wrapped.getPort();
    }

    @Override
    public String getHandlerName() {
        return "onServerJoin";
    }
}
