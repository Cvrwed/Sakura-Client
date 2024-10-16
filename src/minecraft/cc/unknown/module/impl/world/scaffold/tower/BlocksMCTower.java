package cc.unknown.module.impl.world.scaffold.tower;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class BlocksMCTower extends Mode<Scaffold> {

	private int tower = 5;

	public BlocksMCTower(String name, Scaffold parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mc.gameSettings.keyBindJump.isKeyDown() && PlayerUtil.blockNear(2) && !MoveUtil.isMoving()) {
			if (mc.player.posY % 1 <= 0.00153598) {
				mc.player.setPosition(mc.player.posX, Math.floor(mc.player.posY), mc.player.posZ);
				mc.player.motionY = 0.42F;
			} else if (mc.player.posY % 1 < 0.1 && mc.player.offGroundTicks != 0) {
				mc.player.motionY = 0;
				mc.player.setPosition(mc.player.posX, Math.floor(mc.player.posY), mc.player.posZ);
			}
		}

		if (MoveUtil.isMoving() && mc.gameSettings.keyBindJump.isKeyDown()) {
			if (mc.player.onGround) {
				mc.player.jump();
			}
		}

	};

	@EventLink
	public final Listener<PacketEvent> onPacketSend = event -> {
		final Packet<?> packet = event.getPacket();
		if (!event.isSend())
			return;

		if (MoveUtil.isMoving()) {
			if (mc.player.motionY > -0.09800000190734864 && packet instanceof C08PacketPlayerBlockPlacement) {
				final C08PacketPlayerBlockPlacement wrapper = ((C08PacketPlayerBlockPlacement) packet);

				if (wrapper.getPosition().equals(new BlockPos(mc.player.posX, mc.player.posY - 1.4, mc.player.posZ))) {
					mc.player.motionY = -0.09800000190734864;
				}
			}
		}

	};
}
