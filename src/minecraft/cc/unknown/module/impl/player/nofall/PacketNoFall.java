package cc.unknown.module.impl.player.nofall;

import cc.unknown.component.impl.player.FallDistanceComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.module.impl.player.NoFall;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Auth
 * @since 3/02/2022
 */
public class PacketNoFall extends Mode<NoFall> {

	public PacketNoFall(String name, NoFall parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			float distance = FallDistanceComponent.distance;

			if (distance > 3) {
				PacketUtil.send(new C03PacketPlayer(true));
				distance = 0;
			}

			FallDistanceComponent.distance = distance;
		}
	};
}