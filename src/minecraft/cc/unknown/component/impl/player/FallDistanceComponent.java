package cc.unknown.component.impl.player;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;

public final class FallDistanceComponent extends Component {

	public static float distance;

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<MotionEvent> onPreMotionEvent = event -> {
		if (event.isPre()) {
			final double fallDistance = mc.player.lastTickPosY - mc.player.posY;

			if (fallDistance > 0) {
				distance += fallDistance;
			}

			if (event.isOnGround()) {
				distance = 0;
			}
		}
	};
}
