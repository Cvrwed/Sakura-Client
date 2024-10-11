package cc.unknown.event.impl.motion;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptHitSlowDownEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class HitSlowDownEvent extends CancellableEvent {
    public double slowDown;
    public boolean sprint;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptHitSlowDownEvent(this);
    }
}
