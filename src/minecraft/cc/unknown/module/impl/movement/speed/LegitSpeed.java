package cc.unknown.module.impl.movement.speed;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.impl.movement.Speed;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.value.Mode;

public class LegitSpeed extends Mode<Speed> {

    public LegitSpeed(String name, Speed parent) {
        super(name, parent);
    }

    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<PreUpdateEvent> preUpdate = event -> {
    	if (!mc.player.onGround) {
    		RotationComponent.setRotations(new Vector2f(mc.player.rotationYaw + 45, mc.player.rotationPitch), 10, MovementFix.SILENT);
    	}
    	mc.player.jumpTicks = 0;
    };

    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<PreStrafeEvent> strafe = event -> {
        if (mc.player.onGround && MoveUtil.isMoving()) {
            mc.player.jump();
        }
    };
}