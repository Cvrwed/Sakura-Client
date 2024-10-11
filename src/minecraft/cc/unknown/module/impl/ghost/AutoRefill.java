package cc.unknown.module.impl.ghost;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.ClickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

@ModuleInfo(aliases = "Auto Refill", description = "refill ur pots", category = Category.GHOST)
public class AutoRefill extends Module {
    private NumberValue delay = new NumberValue("Delay", this, 0, 100, 1000, 1);
    private BooleanValue invOpen = new BooleanValue("Inventory Open", this, true);
    private StopWatch timer = new StopWatch();

    @EventLink
    public final Listener<ClickEvent> onClick = event -> {
        for (ItemStack itemStack : mc.player.inventory.mainInventory) {
            if ((!invOpen.getValue() || mc.currentScreen instanceof GuiInventory) && !PlayerUtil.isHotbarFull() && itemStack != null &&
                    itemStack.getItem() == Item.getItemById(373) && timer.finished(delay.getValue().longValue())) {

                refill();
                timer.reset();
            }
        }
    };
    
    private void refill() {
        for(int i = 9; i < 37; ++i) {
            ItemStack itemstack = mc.player.inventoryContainer.getSlot(i).getStack();

            if (itemstack != null && itemstack.getItem() == Items.potionitem && ItemPotion.isSplash(itemstack.getMetadata())) {
                mc.playerController.windowClick(0, i, 0, 1, mc.player);
                break;
            }
        }
    }
}