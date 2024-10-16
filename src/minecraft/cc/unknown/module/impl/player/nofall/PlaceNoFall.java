package cc.unknown.module.impl.player.nofall;

import cc.unknown.component.impl.player.FallDistanceComponent;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.impl.player.NoFall;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

/**
 * @author Auth
 * @since 3/02/2022
 */
public class PlaceNoFall extends Mode<NoFall> {

	public PlaceNoFall(String name, NoFall parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		float distance = FallDistanceComponent.distance;

		if (distance > 3) {
			PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(event.getPosX(), event.getPosY(),
					event.getPosZ(), event.getYaw(), event.getPitch(), true));
			PacketUtil.send(new C08PacketPlayerBlockPlacement(getComponent(Slot.class).getItemStack()));
			distance = 0;
		}

		FallDistanceComponent.distance = distance;

	};
}