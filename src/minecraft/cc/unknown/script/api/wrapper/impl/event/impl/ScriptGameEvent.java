package cc.unknown.script.api.wrapper.impl.event.impl;

import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptGameEvent extends ScriptEvent<GameEvent> {

    public ScriptGameEvent(final GameEvent wrappedEvent) {
        super(wrappedEvent);
    }

    @Override
    public String getHandlerName() {
        return "onGame";
    }
}
