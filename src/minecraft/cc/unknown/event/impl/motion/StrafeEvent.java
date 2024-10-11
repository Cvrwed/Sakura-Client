package cc.unknown.event.impl.motion;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptStrafeEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.MoveUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class StrafeEvent extends CancellableEvent implements Accessor {
    private float forward;
    private float strafe;
    private float friction;
    private float yaw;

    public void setSpeed(final double speed, final double motionMultiplier) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        mc.player.motionX *= motionMultiplier;
        mc.player.motionZ *= motionMultiplier;
    }

    public void setSpeed(final double speed) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        MoveUtil.stop();
    }
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptStrafeEvent(this);
    }
}
