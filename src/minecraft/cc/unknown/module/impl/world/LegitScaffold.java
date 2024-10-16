package cc.unknown.module.impl.world;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

@ModuleInfo(aliases = "Legit Scaffold", description = "Sneaks at the edge of blocks", category = Category.WORLD)
public class LegitScaffold extends Module {

	private final BoundsNumberValue delay = new BoundsNumberValue("Delay", this, 100, 200, 0, 500, 1);
	private final BooleanValue pitchCheck = new BooleanValue("Pitch Check", this, true);
	private final BoundsNumberValue pitchRange = new BoundsNumberValue("Pitch Range", this, 70, 85, 0, 90, 1,
			() -> !pitchCheck.getValue());
	private final BooleanValue legit = new BooleanValue("Legitimize", this, true);
	private final BooleanValue holdShift = new BooleanValue("Hold Shift", this, false);
	private final BooleanValue slotSwap = new BooleanValue("Block Switching", this, true);
	private final BooleanValue blocksOnly = new BooleanValue("Blocks Only", this, true);
	private final BooleanValue backwards = new BooleanValue("Backwards Movement Only", this, true);

	private boolean shouldBridge, isShifting = false;
	private StopWatch shiftTimer = new StopWatch();

	@Override
	public void onDisable() {
		setSneak(false);
		if (PlayerUtil.isOverAir()) {
			setSneak(false);
		}

		shouldBridge = false;
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		if (!(mc.currentScreen == null) || !isInGame())
			return;

		boolean shift = delay.getSecondValue().intValue() > 0;

		if (mc.player.rotationPitch < pitchRange.getValue().floatValue()
				|| mc.player.rotationPitch > pitchRange.getSecondValue().floatValue()) {
			shouldBridge = false;
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				setSneak(true);
			}
			return;
		}

		if (holdShift.getValue()) {
			if (!Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				shouldBridge = false;
				return;
			}
		}

		if (mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) {
			return;
		}

		if (blocksOnly.getValue()) {
			ItemStack i = mc.player.getHeldItem();
			if (i == null || !(i.getItem() instanceof ItemBlock)) {
				if (isShifting) {
					isShifting = false;
					setSneak(false);
				}
				return;
			}
		}

		if (backwards.getValue()) {
			if ((mc.player.movementInput.moveForward > 0) && (mc.player.movementInput.moveStrafe == 0)
					|| mc.player.movementInput.moveForward >= 0) {
				shouldBridge = false;
				return;
			}
		}

		if (mc.player.onGround) {
			if (PlayerUtil.isOverAir()) {
				if (shift) {
					shiftTimer.setMillis(MathHelper.randomInt(delay.getValue().intValue(),
							(int) (delay.getSecondValue().intValue() + 0.1)));
					shiftTimer.reset();
				}

				isShifting = true;
				setSneak(true);
				shouldBridge = true;
			} else if (mc.player.isSneaking() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())
					&& holdShift.getValue()) {
				isShifting = false;
				shouldBridge = false;
				setSneak(false);
			} else if (holdShift.getValue() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				isShifting = false;
				shouldBridge = false;
				setSneak(false);
			} else if (mc.player.isSneaking()
					&& (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && holdShift.getValue())
					&& (!shift || shiftTimer.hasFinished())) {
				isShifting = false;
				setSneak(false);
				shouldBridge = true;
			} else if (mc.player.isSneaking() && !holdShift.getValue() && (!shift || shiftTimer.hasFinished())) {
				isShifting = false;
				setSneak(false);
				shouldBridge = true;
			}
		} else if (shouldBridge && mc.player.capabilities.isFlying) {
			setSneak(false);
			shouldBridge = false;
		} else if (shouldBridge && PlayerUtil.isOverAir() && legit.getValue()) {
			isShifting = true;
			setSneak(true);
		} else {
			isShifting = false;
			setSneak(false);
		}

	};

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		if (!isInGame())
			return;

		if (slotSwap.getValue() && shouldSkipBlockCheck())
			swapToBlock();

		if (mc.currentScreen != null || mc.player.getHeldItem() == null)
			return;
	};

	private void swapToBlock() {
		for (int slot = 0; slot <= 8; slot++) {
			ItemStack itemInSlot = mc.player.inventory.getStackInSlot(slot);
			if (itemInSlot != null && itemInSlot.getItem() instanceof ItemBlock && itemInSlot.stackSize > 0) {
				ItemBlock itemBlock = (ItemBlock) itemInSlot.getItem();
				Block block = itemBlock.getBlock();
				if (mc.player.inventory.currentItem != slot && block.isFullCube()) {
					mc.player.inventory.currentItem = slot;
				} else {
					return;
				}
				return;
			}
		}
	}

	private boolean shouldSkipBlockCheck() {
		ItemStack heldItem = mc.player.getHeldItem();
		return heldItem == null || !(heldItem.getItem() instanceof ItemBlock);
	}

	private boolean shouldPitchCheck() {
		boolean maxPitch = mc.player.rotationPitch > pitchRange.getValue().floatValue();
		boolean minPitch = mc.player.rotationPitch < pitchRange.getValue().floatValue();
		return (maxPitch || minPitch);
	}

	private boolean shouldBridgeCheck() {
		double moveForward = mc.player.movementInput.moveForward;
		double moveStrafe = mc.player.movementInput.moveStrafe;
		return (moveForward > 0 && moveStrafe == 0);
	}

	private void setSneak(boolean sneak) {
		mc.gameSettings.keyBindSneak.setPressed(sneak);
	}

}