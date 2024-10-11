package cc.unknown.event.impl.motion;

import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptPreUpdateEvent;

public class PreUpdateEvent implements Event {
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptPreUpdateEvent(this);
    }
}
