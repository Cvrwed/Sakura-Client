package cc.unknown.event;

import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public interface Event {
    default ScriptEvent<? extends Event> getScriptEvent() {
        return null;
    }
}
