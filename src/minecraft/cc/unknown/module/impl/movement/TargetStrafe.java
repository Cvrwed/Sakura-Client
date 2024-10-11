package cc.unknown.module.impl.movement;

import java.util.List;

import cc.unknown.component.impl.player.TargetComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.JumpEvent;
import cc.unknown.event.impl.motion.PreUpdateEvent;
import cc.unknown.event.impl.motion.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.util.vector.Vector3d;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

/**
 * @author Alan
 * @since 20/10/2021
 */

@ModuleInfo(aliases = {"Target Strafe"}, description = "Strafes around the target you're currently attacking", category = Category.MOVEMENT)
public class TargetStrafe extends Module {

    private final NumberValue range = new NumberValue("Range", this, 1, 0.2, 6, 0.1);

    public final BooleanValue holdJump = new BooleanValue("Hold Jump", this, true);
    private float yaw;
    private EntityLivingBase target;
    private boolean left, colliding;
    private boolean active;

    @EventLink(value = Priority.HIGH)
    public final Listener<JumpEvent> onJump = event -> {
        if (target != null && active) {
            event.setYaw(yaw);
        }
    };

    @EventLink(value = Priority.HIGH)
    public final Listener<StrafeEvent> onStrafe = event -> {
        if (target != null && active) {
            event.setYaw(yaw);
        }
    };

    @EventLink(value = Priority.HIGH)
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        // Disable if scaffold is enabled
        Module scaffold = getModule(Scaffold.class);
        KillAura killaura = getModule(KillAura.class);

        if (scaffold == null || scaffold.isEnabled() || killaura == null || !killaura.isEnabled()) {
            active = false;
            return;
        }

        active = true;

        /*
         * Getting targets and selecting the nearest one
         */
        Module speed = getModule(Speed.class);
        Module test = null;
        Module flight = getModule(Flight.class);

        if (holdJump.getValue() && !mc.gameSettings.keyBindJump.isKeyDown() || !(mc.gameSettings.keyBindForward.isKeyDown() &&
                ((flight != null && flight.isEnabled()) || ((speed != null && speed.isEnabled()) || (test != null && test.isEnabled()))))) {
            target = null;
            return;
        }

        final List<EntityLivingBase> targets = TargetComponent.getTargets(this.range.getValue().doubleValue() + 6);

        if (targets.isEmpty()) {
            target = null;
            return;
        }

        if (mc.player.isCollidedHorizontally || !PlayerUtil.isBlockUnder(5, false)) {
            if (!colliding) {
                MoveUtil.strafe();
                left = !left;
            }
            colliding = true;
        } else {
            colliding = false;
        }

        target = targets.get(0);

        if (target == null) {
            return;
        }

        float yaw = RotationUtil.calculate(target).getX() + (90 + 45) * (left ? -1 : 1);

        final double range = this.range.getValue().doubleValue() + Math.random() / 100f;
        final double posX = -MathHelper.sin((float) Math.toRadians(yaw)) * range + target.posX;
        final double posZ = MathHelper.cos((float) Math.toRadians(yaw)) * range + target.posZ;

        yaw = RotationUtil.calculate(new Vector3d(posX, target.posY, posZ)).getX();

        this.yaw = yaw;
        mc.player.movementYaw = this.yaw;
    };
}