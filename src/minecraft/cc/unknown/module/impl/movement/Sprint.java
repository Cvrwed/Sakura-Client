package cc.unknown.module.impl.movement;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.motion.PreUpdateEvent;
import cc.unknown.event.impl.motion.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;

@ModuleInfo(aliases = "Sprint", description = "Makes you sprint", category = Category.MOVEMENT)
public class Sprint extends Module {

		private ModeValue mode = new ModeValue("Mode", this)
				.add(new SubMode("Legit"))
				.add(new SubMode("OmniSprint"))
				.add(new SubMode("Legit OmniSprint"))
				.setDefault("Legit");
	    
    private float forward = 0;
    private float strafe = 0;

    @EventLink(value = Priority.LOW)
    public final Listener<StrafeEvent> onStrafe = event -> {
    	if (mode.is("Legit") || mode.is("Legit OmniSprint")) mc.gameSettings.keyBindSprint.setPressed(true);
    	
    	if (mode.is("OmniSprint")) {
            mc.player.omniSprint = MoveUtil.isMoving();

            MoveUtil.preventDiagonalSpeed();

            mc.player.setSprinting(MoveUtil.isMoving() && !mc.player.isCollidedHorizontally &&
                    !mc.player.isSneaking() && !mc.player.isUsingItem());
    	}
    };

    @Override
    public void onDisable() {
        mc.player.setSprinting(mc.gameSettings.keyBindSprint.isKeyDown());
        mc.player.omniSprint = false;
    }

    @EventLink(value = Priority.HIGH)
    public final Listener<MoveInputEvent> moveInput = event -> {
    	if (mode.is("Legit OmniSprint")) {
	        forward = event.getForward();
	        strafe = event.getStrafe();
    	}
    };
    
    @EventLink(value = Priority.LOW)
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
    	if (mode.is("Legit OmniSprint")) {
	        RotationComponent.setRotations(new Vector2f((float) Math.toDegrees(MoveUtil.direction(forward, strafe)), mc.player.rotationPitch),
	                10, MovementFix.SILENT);
    	}
    };

}