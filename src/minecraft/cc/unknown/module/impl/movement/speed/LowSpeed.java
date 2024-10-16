package cc.unknown.module.impl.movement.speed;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.impl.movement.Speed;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;

/**
 * @author Alan
 * @since 18/11/2022
 */

public class LowSpeed extends Mode<Speed> {
	public LowSpeed(String name, Speed parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mc.player.onGround)
			mc.player.jump();
		MoveUtil.strafe(0.26F);

	};
}