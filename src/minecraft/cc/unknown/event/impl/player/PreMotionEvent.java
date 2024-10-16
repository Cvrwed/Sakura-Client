package cc.unknown.event.impl.player;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.player.ScriptPreMotionEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PreMotionEvent extends CancellableEvent {
    private double posX;
    private double posY;
    private double posZ;
    private float yaw;
    private float pitch;
    private boolean onGround;
    private boolean isSprinting;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptPreMotionEvent(this);
    }
}