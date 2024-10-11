package cc.unknown.module.impl.player.nofall;

import cc.unknown.component.impl.player.FallDistanceComponent;
import cc.unknown.component.impl.player.PingSpoofComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.impl.player.NoFall;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Alan
 * @since 3/02/2022
 */
public class WatchdogNoFall extends Mode<NoFall> {

	private int blinkTicks = 0;

	private boolean start;

	public WatchdogNoFall(String name, NoFall parent) {
		super(name, parent);
	}

	public final BooleanValue LessFall = new BooleanValue("Packet", this, true);

	@EventLink
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			if (!PlayerUtil.isBlockUnder() || getModule(Scaffold.class).isEnabled()) {
				return;
			}

			if (this.mc.player.offGroundTicks == 1 && mc.player.motionY < 0 && PlayerUtil.isBlockUnder()
					&& !PlayerUtil.isBlockUnder(3)) {
				start = true;
			}

			if (start) {
				PingSpoofComponent.spoof(99999, true, false, false, false, true);
				event.setOnGround(true);
				blinkTicks++;
			}

			if (start && mc.player.onGround) {
				PingSpoofComponent.dispatch();
				start = false;
				blinkTicks = 0;
			}

			if (!(blinkTicks > 0) && (FallDistanceComponent.distance > 3) && LessFall.getValue()) {
				PacketUtil.send(new C03PacketPlayer(true));

				mc.timer.timerSpeed = 0.5f;
				FallDistanceComponent.distance = 0;
			}
		}
	};

	@EventLink
	public final Listener<Render2DEvent> event = event -> {
		if (blinkTicks > 0) {
			mc.fontRendererObj.drawCentered("Blinking: " + blinkTicks,
					(double) mc.scaledResolution.getScaledWidth() / 2,
					(double) mc.scaledResolution.getScaledHeight() / 2 + 20, -1);
		}
	};
}