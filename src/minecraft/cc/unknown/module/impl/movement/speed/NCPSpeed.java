package cc.unknown.module.impl.movement.speed;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.StrafeEvent;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.impl.movement.Speed;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

/**
 * @author Auth
 * @since 18/11/2021
 */

public class NCPSpeed extends Mode<Speed> {

    private final NumberValue jumpMotion = new NumberValue("Jump Motion", this, 0.4, 0.4, 0.42, 0.01);
    private final NumberValue groundSpeed = new NumberValue("Ground Speed", this, 1.75, 0.1, 2.5, 0.05);
    private final NumberValue bunnySlope = new NumberValue("Bunny Slope", this, 0.66, 0, 1, 0.01);
    private final NumberValue timer = new NumberValue("Timer", this, 1, 0.1, 10, 0.05);
    private final BooleanValue boost = new BooleanValue("Damage Boost", this, true);
    private final BooleanValue hurtBoost = new BooleanValue("Custom Boost", this, false);
    private final NumberValue boostSpeed = new NumberValue("Boost Speed", this, .8, 0.1, 9.5, 0.1);

    private final BooleanValue lowHop = new BooleanValue("Low Hop", this, false);

    private final BooleanValue yPort = new BooleanValue("Y-port Hop", this, false);
    private final NumberValue hurTime = new NumberValue("Hurt Time", this, 6, 1, 10, 1);

    private boolean reset;
    private double speed;

    public NCPSpeed(String name, Speed parent) {
        super(name, parent);
    }

    @EventLink
    public final Listener<PacketEvent> onPacketReceive = event -> {
        if (!boost.getValue()) return;

        final Packet<?> packet = event.getPacket();
	    if (!event.isReceive()) return;

        if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity wrapper = ((S12PacketEntityVelocity) packet);
            if (wrapper.getEntityID() == mc.player.getEntityId()) {
                speed = Math.hypot(wrapper.motionX / 8000.0D, wrapper.motionZ / 8000.0D);
            }
        }
    };


    @EventLink
    public final Listener<StrafeEvent> onStrafe = event -> {
        if (lowHop.getValue()){
            if (mc.player.offGroundTicks == 4){
                mc.player.motionY = -0.09800000190734864;
            }
        }

        if (yPort.getValue() && mc.player.offGroundTicks == 5 && Math.abs(mc.player.motionY - 0.09800000190734864) < 0.12){
            mc.player.motionY = -0.09800000190734864;
        }

        if (hurtBoost.getValue() && mc.player.ticksSincePlayerVelocity <= hurTime.getValue().intValue()) {
            speed = boostSpeed.getValue().doubleValue();
        }

        final double base = MoveUtil.getAllowedHorizontalDistance();

        if (MoveUtil.isMoving()) {
            switch (mc.player.offGroundTicks) {
                case 0:
                    float jumpMotion = this.jumpMotion.getValue().floatValue();

                    float motion = mc.player.isCollidedHorizontally ? 0.42F : jumpMotion == 0.4f ? jumpMotion : 0.42f;
                    mc.player.motionY = MoveUtil.jumpBoostMotion(motion);
                    speed = base * groundSpeed.getValue().doubleValue();
                    break;

                case 1:
                    speed -= (bunnySlope.getValue().doubleValue() * (speed - base));
                    break;

                default:
                    speed -= speed / MoveUtil.BUNNY_FRICTION;
                    break;
            }

            mc.timer.timerSpeed = timer.getValue().floatValue();
            reset = false;
        } else if (!reset) {
            speed = MoveUtil.getAllowedHorizontalDistance();
            mc.timer.timerSpeed = 1;
            reset = true;
        }

        if (mc.player.isCollidedHorizontally) {
            speed = MoveUtil.getAllowedHorizontalDistance();
        }

        event.setSpeed(Math.max(speed, base), Math.random() / 2000);
    };

    @EventLink
    public final Listener<TeleportEvent> onTeleport = event -> {
        speed = 0;
    };

    @Override
    public void onDisable() {
        speed = 0;
    }
}