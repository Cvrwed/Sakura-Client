package cc.unknown.event.impl.input;

import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.input.ScriptMoveInputEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MoveInputEvent implements Event {
    private float forward, strafe;
    private boolean jump, sneak;
    private double sneakSlowDownMultiplier;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptMoveInputEvent(this);
    }
}
