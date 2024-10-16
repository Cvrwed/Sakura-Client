package cc.unknown.module.impl.movement.flight;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.impl.movement.Flight;
import cc.unknown.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author Nicklas
 * @since 19.08.2022
 */

public class AirJumpFlight extends Mode<Flight> {
	private double y;

	public AirJumpFlight(String name, Flight parent) {
		super(name, parent);
	}

	@Override
	public void onEnable() {
		y = Math.floor(mc.player.posY);
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()) {
			y = mc.player.posY;
		}

		if (mc.player.onGround) {
			mc.player.jump();
		}

	};

	@EventLink
	public final Listener<BlockAABBEvent> onBlockAABB = event -> {
		if (event.getBlock() instanceof BlockAir && !mc.gameSettings.keyBindSneak.isKeyDown()
				&& (mc.player.posY < y + 1 || mc.gameSettings.keyBindJump.isKeyDown())) {
			final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

			if (y < mc.player.posY) {
				event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
			}
		}
	};

	@EventLink
	public final Listener<MoveInputEvent> onMove = event -> {
		event.setSneak(false);
	};
}