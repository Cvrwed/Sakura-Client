package cc.unknown.event.impl.input;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptClickEvent;

public final class ClickEvent extends CancellableEvent {
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptClickEvent(this);
    }
}
