package cc.unknown.module.impl.movement;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.motion.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.util.vector.Vector3d;
import cc.unknown.value.impl.BooleanValue;

@ModuleInfo(aliases = "Sprint", description = "Makes you sprint", category = Category.MOVEMENT)
public class Sprint extends Module {

    private final BooleanValue legit = new BooleanValue("Legit", this, true);
    private final BooleanValue rotate = new BooleanValue("Rotate", this, false, () -> legit.getValue());

    @EventLink(value = Priority.LOW)
    public final Listener<StrafeEvent> onStrafe = event -> {
        mc.gameSettings.keyBindSprint.setPressed(true);

        if (!legit.getValue()) {
            mc.player.omniSprint = MoveUtil.isMoving();

            MoveUtil.preventDiagonalSpeed();

            mc.player.setSprinting(!legit.getValue() && MoveUtil.isMoving() && !mc.player.isCollidedHorizontally &&
                    !mc.player.isSneaking() && !mc.player.isUsingItem());
        }
    };

    @Override
    public void onDisable() {
        mc.player.setSprinting(mc.gameSettings.keyBindSprint.isKeyDown());
        mc.player.omniSprint = false;
    }

}