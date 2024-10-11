package cc.unknown.module.impl.movement.speed;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.StrafeEvent;
import cc.unknown.module.impl.movement.Speed;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;
import cc.unknown.value.impl.NumberValue;

/**
 * @author Auth
 * @since 18/11/2021
 */

public class VanillaSpeed extends Mode<Speed> {

    private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 9.5, 0.1);

    public VanillaSpeed(String name, Speed parent) {
        super(name, parent);
    }

    @EventLink
    public final Listener<StrafeEvent> onStrafe = event -> {
        if (MoveUtil.isMoving() && mc.player.onGround) {
            this.mc.player.jump();
        }

        event.setSpeed(speed.getValue().floatValue());
    };
}