package cc.unknown.module.impl.player.antivoid;

import cc.unknown.component.impl.player.FallDistanceComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.module.impl.player.AntiVoid;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;
import cc.unknown.value.impl.NumberValue;

public class CollisionAntiVoid extends Mode<AntiVoid> {

	private final NumberValue distance = new NumberValue("Distance", this, 5, 0, 10, 1);

	public CollisionAntiVoid(String name, AntiVoid parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			if (FallDistanceComponent.distance > distance.getValue().intValue() && !PlayerUtil.isBlockUnder()
					&& mc.player.posY + mc.player.motionY < Math.floor(mc.player.posY)) {
				mc.player.motionY = Math.floor(mc.player.posY) - mc.player.posY;
				if (mc.player.motionY == 0) {
					mc.player.onGround = true;
					event.setOnGround(true);
				}
			}
		}
	};
}