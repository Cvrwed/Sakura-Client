package cc.unknown.module.impl.movement.flight;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.other.BlockAABBEvent;
import cc.unknown.event.impl.other.MoveEvent;
import cc.unknown.module.impl.movement.Flight;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author Nicklas
 * @since 31.03.2022
 */

public class VerusFlight extends Mode<Flight> { // TODO: make sneaking go down

	private int ticks = 0;

	public VerusFlight(String name, Flight parent) {
		super(name, parent);
	}

	@Override
	public void onDisable() {
		MoveUtil.stop();
	}

	@EventLink
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				if (mc.player.ticksExisted % 2 == 0) {
					mc.player.motionY = 0.42F;
				}
			}

			++ticks;
		}
	};

	@EventLink
	public final Listener<MoveEvent> onMove = event -> {
		if (mc.player.onGround && ticks % 14 == 0) {
			event.setPosY(0.42F);
			MoveUtil.strafe(0.69);
			mc.player.motionY = -(mc.player.posY - Math.floor(mc.player.posY));
		} else {
			// A Slight Speed Boost.
			if (mc.player.onGround) {
				MoveUtil.strafe(1.01 + MoveUtil.speedPotionAmp(0.15));
				// Slows Down To Not Flag Speed11A.
			} else
				MoveUtil.strafe(0.41 + MoveUtil.speedPotionAmp(0.05));
		}

		mc.player.setSprinting(true);
		mc.player.omniSprint = true;

		ticks++;
	};

	@EventLink
	public final Listener<BlockAABBEvent> onBlockAABB = event -> {
		if (event.getBlock() instanceof BlockAir && !mc.gameSettings.keyBindSneak.isKeyDown()
				|| mc.gameSettings.keyBindJump.isKeyDown()) {
			final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

			if (y < mc.player.posY) {
				event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
			}
		}

	};

	@EventLink
	public final Listener<MoveInputEvent> onMoveInput = event -> {
		// Sets Sneaking To False So That We Can't Sneak When Flying Because That Can
		// Cause Flags.
		event.setSneak(false);
	};
}