package cc.unknown.module.impl.movement.flight;

import cc.unknown.component.impl.player.BlinkComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.movement.Flight;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Alan
 * @since 18/11/2021
 */

public class MMCFlight extends Mode<Flight> {

	private boolean clipped;
	private int ticks;

	public MMCFlight(String name, Flight parent) {
		super(name, parent);
	}

	@Override
	public void onEnable() {
		clipped = false;
		ticks = 0;
	}

	@Override
	public void onDisable() {
		BlinkComponent.blinking = false;
		MoveUtil.stop();
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		ticks++;

		if (mc.player.onGround) {
			MoveUtil.stop();
		} else {
			return;
		}

		if (ticks == 1) {
			if (PlayerUtil.blockRelativeToPlayer(0, -2.5, 0).isFullBlock()) {
				mc.timer.timerSpeed = 0.1F;
				BlinkComponent.blinking = true;

				PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY,
						mc.player.posZ, true));
				PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX,
						MoveUtil.roundToGround(mc.player.posY - (2.5 - (Math.random() / 100))), mc.player.posZ, false));
				PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY,
						mc.player.posZ, false));

				clipped = true;

				mc.player.jump();
				MoveUtil.strafe(7 - Math.random() / 10);
			}
		}

	};

	@EventLink
	public final Listener<PreStrafeEvent> onStrafe = event -> {
		MoveUtil.strafe();
	};

	@EventLink
	public final Listener<TeleportEvent> onTeleport = event -> {
		if (clipped) {
			event.setCancelled();
			clipped = false;
		}
	};
}