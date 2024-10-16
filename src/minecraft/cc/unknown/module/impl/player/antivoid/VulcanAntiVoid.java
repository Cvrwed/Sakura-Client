package cc.unknown.module.impl.player.antivoid;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.movement.Flight;
import cc.unknown.module.impl.movement.Speed;
import cc.unknown.module.impl.player.AntiVoid;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author Strikeless
 * @since 18.03.2022
 */
public class VulcanAntiVoid extends Mode<AntiVoid> {

	private final NumberValue distance = new NumberValue("Distance", this, 2.6, 0, 10, 0.1);

	private boolean teleported;

	private boolean noBlock;

	private Flight flight = null;
	private Speed speed = null;

	private boolean speedWasEnabled = false;
	private int disable;

	public VulcanAntiVoid(String name, AntiVoid parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (flight == null) {
			flight = getModule(Flight.class);
		}
		if (speed == null) {
			speed = getModule(Speed.class);
		}

		if (mc.player.fallDistance > distance.getValue().floatValue() && !PlayerUtil.isBlockUnder()) {

			noBlock = true;
		}

		if (flight.isEnabled()) {
			noBlock = false;
		}

		if (speed.isEnabled() && noBlock) {
			speedWasEnabled = true;
			speed.toggle();
		}

		if (!noBlock && !(speed.isEnabled()) && speedWasEnabled) {
			speed.toggle();
			speedWasEnabled = false;
		}

	};

	@EventLink
	public final Listener<BlockAABBEvent> onBlockAABB = event -> {

		// Sets The Bounding Box To The Players Y Position.
		if (event.getBlock() instanceof BlockAir && !mc.player.isSneaking() && noBlock) {
			final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

			if (y < mc.player.posY) {
				event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
			}
		}

		if (!(event.getBlock() instanceof BlockAir && !mc.player.isSneaking()) && noBlock
				&& !mc.player.isCollidedHorizontally) {
			noBlock = false;
		}
	};

	@EventLink
	public final Listener<PreStrafeEvent> onStrafe = event -> {
		if (noBlock) {
			MoveUtil.strafe(.1);
			if (mc.player.ticksExisted % 2 == 1 || !(mc.player.moveForward == 0)) {

				event.setForward(1);
			} else {
				MoveUtil.strafe(0);
				event.setForward(-1);
			}
		}
	};

	@EventLink
	public final Listener<PacketEvent> onPacketReceive = event -> {
		Packet<?> packet = event.getPacket();
		if (!event.isReceive())
			return;

		if (packet instanceof S08PacketPlayerPosLook) {
			S08PacketPlayerPosLook posLook = ((S08PacketPlayerPosLook) packet);

			noBlock = false;
		}

	};

	@EventLink
	public final Listener<WorldChangeEvent> onWorldChange = event -> {
		noBlock = false;
	};

	@EventLink
	public final Listener<JumpEvent> onJumpEvent = event -> {
		if (noBlock) {
			event.setJumpMotion(0);

		}
	};
}
