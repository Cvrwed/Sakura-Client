package cc.unknown.event.impl.other;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptTickEvent;

public final class TickEvent extends CancellableEvent {
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptTickEvent(this);
    }
}
