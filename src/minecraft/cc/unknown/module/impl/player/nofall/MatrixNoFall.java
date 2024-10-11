package cc.unknown.module.impl.player.nofall;

import cc.unknown.component.impl.player.FallDistanceComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.module.impl.player.NoFall;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;

/**
 * @author Alan
 * @since 3/02/2022
 */
public class MatrixNoFall extends Mode<NoFall> {

	public MatrixNoFall(String name, NoFall parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			float distance = FallDistanceComponent.distance;

			if (PlayerUtil.isBlockUnder()) {
				if (distance > 2) {
					MoveUtil.strafe(0.19);
				}

				if (distance > 3 && MoveUtil.speed() < 0.2) {
					event.setOnGround(true);
					distance = 0;
				}
			}

			FallDistanceComponent.distance = distance;
		}
	};
}