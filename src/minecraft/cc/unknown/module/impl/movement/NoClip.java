package cc.unknown.module.impl.movement;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.player.PushOutOfBlockEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.SlotUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "No Clip", description = "Allows you to pass through normally solid blocks", category = Category.MOVEMENT)
public class NoClip extends Module {

	private BooleanValue block = new BooleanValue("Block", this, false);

	@Override
	public void onDisable() {
		mc.player.noClip = false;
	}

	@EventLink
	public final Listener<BlockAABBEvent> onBlockAABB = event -> {
		if (PlayerUtil.insideBlock()) {
			event.setBoundingBox(null);
			
			if (!(event.getBlock() instanceof BlockAir) && !mc.gameSettings.keyBindSneak.isKeyDown()) {
				final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(),
						z = event.getBlockPos().getZ();

				if (y < mc.player.posY) {
					event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
				}
			}
		}
	};

	@EventLink
	public final Listener<PushOutOfBlockEvent> onPushOutOfBlock = CancellableEvent::setCancelled;

	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		mc.player.noClip = true;

		if (block.getValue()) {
			
			final int slot = SlotUtil.findBlock();
			
			if (getModule(Scaffold.class).isEnabled()) return;
			
			if (getModule(KillAura.class).isEnabled() && getModule(KillAura.class).target != null) return;
			
			if (slot == -1 || PlayerUtil.insideBlock()) {
				return;
			}

			getComponent(Slot.class).setSlot(slot);

			RotationComponent.setRotations(new Vector2f(mc.player.rotationYaw, 90), 2 + Math.random(), MovementFix.SILENT);

			if (RotationComponent.rotations.y >= 89 && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.player.posY == mc.objectMouseOver.getBlockPos().up().getY()) {

				mc.playerController.onPlayerRightClick(mc.player, mc.world, getComponent(Slot.class).getItemStack(), mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec);
				mc.player.swingItem();
			}
			
			
		}
	};
}