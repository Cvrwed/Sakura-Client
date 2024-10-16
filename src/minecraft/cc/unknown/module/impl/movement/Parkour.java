package cc.unknown.module.impl.movement;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.util.BlockPos;

/**
 * @author Alan
 * @since 20/10/2021
 */

@ModuleInfo(aliases = "Parkour", description = ">:3c", category = Category.MOVEMENT)
public class Parkour extends Module {

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		double posX = mc.player.posX;
		double posZ = mc.player.posZ;
		double blockX = Math.floor(posX);
		double blockZ = Math.floor(posZ);

		double difX = posX - blockX;
		double difZ = posZ - blockZ;

		double edgeMargin = 0D;
		double lowerThreshold = 0.2 - edgeMargin;
		double upperThreshold = 0.4 + edgeMargin;

		boolean onEdgeX = difX <= lowerThreshold || difX >= upperThreshold;
		boolean onEdgeZ = difZ <= lowerThreshold || difZ >= upperThreshold;
		BlockPos blockInFront = null;
		if (mc.player.moveForward > 0) {
			blockInFront = mc.player.getPosition().add(mc.player.getLookVec().xCoord, -1,
					mc.player.getLookVec().zCoord);
		} else if (mc.player.moveForward < 0) {
			blockInFront = mc.player.getPosition().add(-mc.player.getLookVec().xCoord, -1,
					-mc.player.getLookVec().zCoord);
		} else if (mc.player.moveStrafing > 0) {
			blockInFront = mc.player.getPosition().add(mc.player.getLookVec().zCoord, -1,
					-mc.player.getLookVec().xCoord);
		} else if (mc.player.moveStrafing < 0) {
			blockInFront = mc.player.getPosition().add(-mc.player.getLookVec().zCoord, -1,
					mc.player.getLookVec().xCoord);
		}

		boolean isBlockAir = blockInFront != null && mc.player.getEntityWorld().isAirBlock(blockInFront);
		boolean shouldJump = (onEdgeX || onEdgeZ);
		if (shouldJump && isBlockAir && mc.player.onGround && mc.player.moveForward > 0 && !mc.player.isSneaking()) {
			mc.gameSettings.keyBindJump.pressed = true;
		} else {
			mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
		}

	};

}
