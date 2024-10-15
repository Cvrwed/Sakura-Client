package cc.unknown.module.impl.combat;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

/**
 * @author Alan
 * @since 29/01/2021
 */

@ModuleInfo(aliases = "Auto Block", description = "Perfect time for block", category = Category.COMBAT)
public class AutoBlock extends Module {
	private final NumberValue chance = new NumberValue("Chance", this, 80, 0, 100, 1);
	private final NumberValue distanceToPlayer = new NumberValue("Distance", this, 3, 1, 6, 0.1);
	
	private Vec3 previousPosition = null;
	private long lastExecutionTime = 0L;

	@EventLink
	public final Listener<MotionEvent> onPre = event -> {
		if (mc.currentScreen != null) return;
		if (!event.isPre()) return;
		if (chance.getValue().intValue() != 100.0D && Math.random() >= chance.getValue().intValue() / 100.0D) return;
		
		if (isPushWithinThreshold()) { }
		
		long currentTime = System.currentTimeMillis();
		if (currentTime - this.lastExecutionTime >= 2L ) return;
		
		if (PlayerUtil.isHoldingWeapon() && Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
			float distance = mc.player.getDistanceToEntity(mc.objectMouseOver.entityHit);
			double minDistance = distanceToPlayer.getValue().doubleValue();
			if (distance < minDistance || distance > minDistance) {
				if (mc.gameSettings.keyBindUseItem.isKeyDown())
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
				if (!mc.gameSettings.keyBindAttack.isKeyDown())
					mc.gameSettings.keyBindUseItem.setPressed(true);	
			}
		}
	};

	private boolean isPushWithinThreshold() {
		double pushThreshold = 0.25D;
		if (this.previousPosition == null) {
			this.previousPosition = mc.player.getPositionVector();
			return true;
		}
		Vec3 currentPosition = mc.player.getPositionVector();
		Vec3 movement = currentPosition.subtract(this.previousPosition);
		double pushMagnitude = movement.lengthVector();
		this.previousPosition = currentPosition;
		boolean isWithinThreshold = (pushMagnitude <= pushThreshold);
		if (isWithinThreshold) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - this.lastExecutionTime >= 2L && PlayerUtil.isHoldingWeapon() && isOtherPlayerAttacking()) {
				mc.gameSettings.keyBindUseItem.setPressed(true);
				this.lastExecutionTime = currentTime;				
			}
		} else if (PlayerUtil.isHoldingWeapon()) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
		}
		return isWithinThreshold;
	}

	private boolean isOtherPlayerAttacking() {
		double detectionRadius = distanceToPlayer.getValue().doubleValue();
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (player != mc.player) {
					double distanceToPlayer = mc.player.getDistanceToEntity((Entity) player);
					if (distanceToPlayer <= detectionRadius && player.isSwingInProgress)
						return true;
				}
			}
		}
		return false;
	}
}