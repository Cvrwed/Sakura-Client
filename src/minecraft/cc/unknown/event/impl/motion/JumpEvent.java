package cc.unknown.event.impl.motion;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptJumpEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class JumpEvent extends CancellableEvent {
    private float jumpMotion;
    private float yaw;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptJumpEvent(this);
    }
}
