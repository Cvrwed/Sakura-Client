package cc.unknown.module.impl.combat;

import cc.unknown.component.impl.player.TargetComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.math.MathUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "STap", description = "Keeps you just within range of the target, reduces the potential for incoming attacks.", category = Category.COMBAT)
public final class STap extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Combo"))
            .add(new SubMode("Stop"))
            .setDefault("Stop");
	
    private final NumberValue range = new NumberValue("Range", this, 3, 0, 6, 0.1);
    private final NumberValue combo = new NumberValue("Combo To Start", this, 2, 0, 6, 1);
    
    private int row;

    @EventLink
    public final Listener<MoveInputEvent> onMovementInput = event -> {
        EntityLivingBase target = TargetComponent.getTarget(10);
        double range = this.range.getValue().doubleValue();

        if (mc.player == null) return;
        if (!mc.player.onGround) return;
        
        if (mc.player.ticksSinceAttack <= 7) range -= 0.2;

        if (target == null) {
            row = 0;
            return;
        }

        if (target.hurtTime > 0) row += 1;
        if (mc.player.hurtTime > 0) row = 0;

        if (row <= combo.getValue().intValue() * 8 && combo.getValue().intValue() > 0) {
            return;
        }

        if (PlayerUtil.calculatePerfectRangeToEntity(target) < range - 0.05) {
            final float forward = event.getForward();
            final float strafe = event.getStrafe();

            final double angle = MathHelper.wrapAngleTo180_double(RotationUtil.calculate(target).getX() - 180);

            if (forward == 0 && strafe == 0) {
                return;
            }

            float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

            for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
                for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                    if (predictedStrafe == 0 && predictedForward == 0) continue;

                    final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MoveUtil.direction(mc.player.rotationYaw, predictedForward, predictedStrafe)));
                    final double difference = MathUtil.wrappedDifference(angle, predictedAngle);

                    if (difference < closestDifference) {
                        closestDifference = (float) difference;
                        closestForward = predictedForward;
                        closestStrafe = predictedStrafe;
                    }
                }
            }

            switch (mode.getValue().getName()) {
                case "Stop":
                    if (closestForward == forward * -1) event.setForward(0);
                    if (closestStrafe == strafe * -1) event.setStrafe(0);
                    break;

                case "Combo":
                    event.setForward(closestForward);
                    event.setStrafe(closestStrafe);
                    break;
            }
        }
    };
}
