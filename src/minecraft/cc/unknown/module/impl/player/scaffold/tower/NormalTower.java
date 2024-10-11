package cc.unknown.module.impl.player.scaffold.tower;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.value.Mode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class NormalTower extends Mode<Scaffold> {

	public NormalTower(String name, Scaffold parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				if (mc.player.onGround) {
					mc.player.motionY = 0.42F;
				}
			}
		}
	};

	@EventLink
	public final Listener<PacketEvent> onPacketSend = event -> {
		final Packet<?> packet = event.getPacket();
	    if (!event.isSend()) return;

		if (mc.player.motionY > -0.0784000015258789 && packet instanceof C08PacketPlayerBlockPlacement) {
			final C08PacketPlayerBlockPlacement wrapper = ((C08PacketPlayerBlockPlacement) packet);

			if (wrapper.getPosition().equals(new BlockPos(mc.player.posX, mc.player.posY - 1.4, mc.player.posZ))) {
				mc.player.motionY = -0.0784000015258789;
			}
		}
	};
}
