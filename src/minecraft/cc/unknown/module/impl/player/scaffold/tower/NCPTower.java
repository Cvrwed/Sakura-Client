package cc.unknown.module.impl.player.scaffold.tower;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

public class NCPTower extends Mode<Scaffold> {

	public NCPTower(String name, Scaffold parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			if (mc.gameSettings.keyBindJump.isKeyDown() && PlayerUtil.blockNear(2)) {
				PacketUtil.sendNoEvent(new C08PacketPlayerBlockPlacement(null));

				if (mc.player.posY % 1 <= 0.00153598) {
					mc.player.setPosition(mc.player.posX, Math.floor(mc.player.posY), mc.player.posZ);
					mc.player.motionY = 0.42F;
				} else if (mc.player.posY % 1 < 0.1 && mc.player.offGroundTicks != 0) {
					mc.player.motionY = 0;
					mc.player.setPosition(mc.player.posX, Math.floor(mc.player.posY), mc.player.posZ);
				}
			}
		}
	};

}
