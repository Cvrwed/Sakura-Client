package cc.unknown.script.api.wrapper.impl.event.impl.other;

import cc.unknown.event.impl.other.KillEvent;
import cc.unknown.script.api.wrapper.impl.ScriptEntity;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptKillEvent extends ScriptEvent<KillEvent> {
    public ScriptEntity getEntity() {
        return new ScriptEntity(this.wrapped.getEntity());
    }

    public ScriptKillEvent(KillEvent wrappedEvent) {
        super(wrappedEvent);
    }

    @Override
    public String getHandlerName() {
        return "onKill";
    }
}
