package cc.unknown.module.impl.movement;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.SlotUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.value.impl.BoundsNumberValue;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Auto Extinguisher", description = "Extinguishes the flame", category = Category.MOVEMENT)
public class AutoExtinguisher extends Module {

	private final BoundsNumberValue rotationSpeed = new BoundsNumberValue("Rotation Speed", this, 2, 3, 0, 10, 1);
	private boolean fire;
	
	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        mc.entityRenderer.getMouseOver(1);

        if (mc.player.isBurning()) {
	        final int slot = findWaterBucket();
	        
			if (slot == -1) {
				return;
			}
	        
			getComponent(Slot.class).setSlot(slot);
	        
	        final float rotationSpeed = this.rotationSpeed.getRandomBetween().floatValue();
	        RotationComponent.setRotations(new Vector2f(mc.player.rotationYaw, 90), rotationSpeed, MovementFix.SILENT);
	
	        if (mc.player.posY == mc.objectMouseOver.getBlockPos().up().getY()) {
	        	if (RotationComponent.rotations.y >= 90 && containsItem(mc.player.getHeldItem(), Items.bucket)) {
	        		PlayerUtil.sendClick(1, true);
	        	} else PlayerUtil.sendClick(1, true);
	        	
	        	PlayerUtil.sendClick(1, false);
	        }
	        
	    	if (!mc.player.isBurning()) {
	    		PlayerUtil.sendClick(1, false);
	
	        }
        }
	};
	
    private int findWaterBucket() {
        if (containsItem(mc.player.getHeldItem(), Items.water_bucket)) {
            return mc.player.inventory.currentItem;
        } else {
            for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
                if (containsItem(mc.player.inventory.mainInventory[i], Items.water_bucket)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean containsItem(ItemStack itemStack, Item item) {
        return itemStack != null && itemStack.getItem() == item;
    }
}