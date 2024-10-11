package cc.unknown.module.impl.movement.speed;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.motion.PreUpdateEvent;
import cc.unknown.event.impl.motion.StrafeEvent;
import cc.unknown.module.impl.movement.Speed;
import cc.unknown.util.player.DamageUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.value.Mode;
import net.minecraft.util.Vec3;

public class SparkySpeed extends Mode<Speed> {
    public SparkySpeed(String name, Speed parent) {
        super(name, parent);
    }

    public Vec3 position = new Vec3(0, 0, 0);
    int jumps = 0;
    float forward = 0;
    float strafe = 0;

    @Override
    public void onEnable() {
        DamageUtil.damagePlayer(DamageUtil.DamageType.POSITION, 3.42F, 1, false, false);
        mc.timer.timerSpeed = 0.2F;
        jumps = 0;
    }

    @EventLink(value = Priority.HIGH)
    Listener<MoveInputEvent> moveInput = event -> {
        forward = event.getForward();
        strafe = event.getStrafe();
    };

    @EventLink
    public final Listener<StrafeEvent> onStrafe = event -> {
        MoveUtil.strafe();

        if (MoveUtil.isMoving() && mc.player.onGround) {
            MoveUtil.strafe();
            mc.player.jump();
            jumps++;
        }

        if (mc.player.offGroundTicks == 5) {
            mc.player.motionY = -0.09800000190734864;

        } else if (mc.player.onGround) {

            mc.player.motionY = .9;
        } else if (mc.player.onGround) {

            mc.player.motionY = .42;

        } else {
            event.setSpeed(.6);
        }

        if (mc.player.hurtTime > 0) {

            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
    };

    @EventLink(value = Priority.LOW)
    Listener<PreUpdateEvent> onPreUpdate = event -> {
        RotationComponent.setRotations(new Vector2f((float) Math.toDegrees(MoveUtil.direction(forward, strafe)), mc.player.rotationPitch),
                10, MovementFix.SILENT);
    };

    @Override
    public void onDisable() {
    	mc.timer.timerSpeed = 1.0F;
    }
}

