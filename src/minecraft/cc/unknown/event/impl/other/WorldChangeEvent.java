package cc.unknown.event.impl.other;

import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.other.ScriptWorldChangeEvent;

public final class WorldChangeEvent implements Event {
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptWorldChangeEvent(this);
    }
}