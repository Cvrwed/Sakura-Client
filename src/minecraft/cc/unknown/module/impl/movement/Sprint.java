package cc.unknown.module.impl.movement;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.value.impl.BooleanValue;

@ModuleInfo(aliases = "Sprint", description = "Makes you sprint", category = Category.MOVEMENT)
public class Sprint extends Module {
		
	private final BooleanValue legit = new BooleanValue("Legit", this, true);
	private final BooleanValue omniLegit = new BooleanValue("Omni Legit", this, false, () -> !legit.getValue());
	    
    private float forward = 0;
    private float strafe = 0;

    @EventLink(value = Priority.LOW)
    public final Listener<PreStrafeEvent> onStrafe = event -> {
    	if (legit.getValue()) mc.gameSettings.keyBindSprint.setPressed(true);
    	
    	if (!legit.getValue()) {
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
    	if (prevent()) return;
    	
    	if (omniLegit.getValue()) {
	        forward = event.getForward();
	        strafe = event.getStrafe();
    	}
    };
    
    @EventLink(value = Priority.LOW)
    public final Listener<PreUpdateEvent> onPreMotion = event -> {
    	if (prevent()) return;
    	
    	if (omniLegit.getValue()) {
	        RotationComponent.setRotations(new Vector2f((float) Math.toDegrees(MoveUtil.direction(forward, strafe)), mc.player.rotationPitch),
	                10, MovementFix.SILENT);
    	}
    };
    
    private boolean prevent() {
    	return (getModule(KillAura.class).isEnabled() && getModule(KillAura.class).target != null) || getModule(Scaffold.class).isEnabled();
    }

}