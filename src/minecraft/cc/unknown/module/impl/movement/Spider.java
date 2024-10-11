package cc.unknown.module.impl.movement;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.other.BlockAABBEvent;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.block.state.IBlockState;
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
		"Spider" }, description = "Allows you to climb up walls like a spider", category = Category.MOVEMENT)
public class Spider extends Module {

	private boolean active;
	private float direction = 0.0F;

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Vulcan"))
			.add(new SubMode("Verus"))
			.add(new SubMode("Kauri"))
			.add(new SubMode("MineMen"))
			.add(new SubMode("Polar"))
			.setDefault("Vulcan");

	@EventLink
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			if (mode.is("kauri")) {
				if (mc.player.isCollidedHorizontally) {
					if (mc.player.ticksExisted % 3 == 0) {
						event.setOnGround(true);
						mc.player.jump();
					}
				}
			}

			if (mode.is("minemen")) {
				if (mc.player.isCollidedHorizontally && !active && mc.player.ticksExisted % 3 == 0) {
					mc.player.motionY = MoveUtil.jumpMotion();
				}

				if (mc.player.isCollidedVertically) {
					active = !mc.player.onGround;
				}
			}

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

			if (mc.player == null) return;
			AxisAlignedBB playerBoundingBox = mc.player.getEntityBoundingBox();
			if (playerBoundingBox == null)
				return;
			boolean isInsideBlock = collideBlockIntersects(playerBoundingBox, null);
			float motion = 0.0F;
			if (isInsideBlock && motion != 0.0F)
				mc.player.motionY = motion;
		}
	};

	@EventLink
	public final Listener<BlockAABBEvent> onBlockBB = e -> {
		if (mode.is("polar")) {
			if (mc.player == null) return;
			if (e.getBlockPos().getY() > mc.player.posY)
				e.setBoundingBox(null);
		}
	};

	@EventLink
	public final Listener<PacketEvent> onPacketSend = e -> {
		if (mode.is("polar")) {
			try {
				if (!e.isSend()) return;
				Packet packet = e.getPacket();
				if (packet instanceof C03PacketPlayer) {
					C03PacketPlayer wrapper = (C03PacketPlayer)packet;
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

	private boolean collideBlockIntersects(AxisAlignedBB axisAlignedBB, Collidable collide) {
		try {
			for (int x = (int)axisAlignedBB.minX; x < (int)axisAlignedBB.maxX + 1; x++) {
				for (int z = (int)axisAlignedBB.minZ; z < (int)axisAlignedBB.maxZ + 1; z++) {
					BlockPos blockPos = new BlockPos(x, (int)axisAlignedBB.minY, z);
					IBlockState blockState = mc.world.getBlockState(blockPos);
					if (collide.test(blockState)) {
						AxisAlignedBB boundingBox = blockState.getBlock().getCollisionBoundingBox(mc.world, blockPos, blockState);
						if (boundingBox != null && axisAlignedBB.intersectsWith(boundingBox))
							return true;
					}
				}
			}
		} catch (NullPointerException ignored) {

		}
		return false;
	}

	private interface Collidable {
		boolean test(IBlockState param1IBlockState);
	}
}