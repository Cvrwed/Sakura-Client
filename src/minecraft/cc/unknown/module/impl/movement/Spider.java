package cc.unknown.module.impl.movement;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

/**
 * @author Alan
 * @since 20/10/2021
 */

@ModuleInfo(aliases = { "Spider",
		"wallclimb" }, description = "Allows you to climb up walls like a spider", category = Category.MOVEMENT)
public class Spider extends Module {

	private boolean active;
	private float direction = 0.0F;

	private final ModeValue mode = new ModeValue("Mode", this).add(new SubMode("Vulcan")).add(new SubMode("Verus"))
			.add(new SubMode("Polar")).setDefault("Vulcan");

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {

		if (mode.is("verus")) {
			if (mc.player.isCollidedHorizontally) {
				if (mc.player.ticksExisted % 2 == 0) {
					mc.player.jump();
				}
			}
		}

		if (mode.is("vulcan")) {
			if (mc.player.isCollidedHorizontally) {
				if (mc.player.ticksExisted % 2 == 0) {
					event.setOnGround(true);
					mc.player.motionY = MoveUtil.jumpMotion();
				}

				final double yaw = MoveUtil.direction();
				event.setPosX(event.getPosX() - -MathHelper.sin((float) yaw) * 0.1f);
				event.setPosZ(event.getPosZ() - MathHelper.cos((float) yaw) * 0.1f);
			}
		}

		if (mode.is("polar")) {
			if (mc.player.isCollidedHorizontally && !insideBlock()) {
				double yaw = MoveUtil.direction();
				mc.player.setPosition(mc.player.posX + -MathHelper.sin((float) yaw) * 0.05, mc.player.posY,
						mc.player.posZ + MathHelper.cos((float) yaw) * 0.05);
				MoveUtil.stop();
				
				mc.gameSettings.keyBindForward.setPressed(false);
				mc.gameSettings.keyBindBack.setPressed(false);
			}
		}

	};

	@EventLink
	public final Listener<TickEvent> onTick = event -> {
		if (mode.is("polar")) {
			if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				if (mc.player.onGround)
					mc.player.motionY += 0.64456D;
				mc.player.motionY -= 0.005D;
			}

			if (mc.player == null)
				return;
			boolean isInsideBlock = insideBlock();
			float motion = 0.0F;
			if (isInsideBlock && motion != 0.0F)
				mc.player.motionY = motion;
		}
	};

	@EventLink
	public final Listener<BlockAABBEvent> onBlockBB = event -> {
		if (mode.is("polar")) {
			if (insideBlock()) {
				BlockPos playerPos = new BlockPos(mc.player);
				BlockPos blockPos = event.getBlockPos();
				if (blockPos.getY() > playerPos.getY())
					event.setBoundingBox(null);
			}
		}
	};

	@EventLink
	public final Listener<PacketEvent> onPacketSend = e -> {
		if (mode.is("polar")) {
			try {
				if (!e.isSend())
					return;
				Packet packet = e.getPacket();
				if (packet instanceof C03PacketPlayer) {
					C03PacketPlayer wrapper = (C03PacketPlayer) packet;
					if (active) {
						float yaw = direction;
						wrapper.setX(Double.valueOf(wrapper.getX() - MathHelper.sin(yaw) * 1.0E-8D).doubleValue());
						wrapper.setZ(Double.valueOf(wrapper.getZ() + MathHelper.cos(yaw) * 1.0E-8D).doubleValue());
						active = false;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private boolean insideBlock(final AxisAlignedBB bb) {
		final WorldClient world = mc.world;
		for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
			for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
				for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
					final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
					final AxisAlignedBB boundingBox;
					if (block != null && !(block instanceof BlockAir)
							&& (boundingBox = block.getCollisionBoundingBox(world, new BlockPos(x, y, z),
									world.getBlockState(new BlockPos(x, y, z)))) != null
							&& bb.intersectsWith(boundingBox)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean insideBlock() {
		if (mc.player.ticksExisted < 5) {
			return false;
		}

		return insideBlock(mc.player.getEntityBoundingBox());
	}
}