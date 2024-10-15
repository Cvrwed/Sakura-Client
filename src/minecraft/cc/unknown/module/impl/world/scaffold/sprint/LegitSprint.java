package cc.unknown.module.impl.world.scaffold.sprint;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.value.Mode;
import net.minecraft.util.MathHelper;

public class LegitSprint extends Mode<Scaffold> {

	public LegitSprint(String name, Scaffold parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			if (Math.abs(MathHelper.wrapAngleTo180_float(mc.player.rotationYaw)
					- MathHelper.wrapAngleTo180_float(RotationComponent.rotations.x)) > 90) {
				mc.gameSettings.keyBindSprint.setPressed(false);
				mc.player.setSprinting(false);
			}
		}
	};
}
