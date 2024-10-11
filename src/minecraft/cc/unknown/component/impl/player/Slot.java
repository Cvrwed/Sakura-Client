package cc.unknown.component.impl.player;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.PreUpdateEvent;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class Slot extends Component {

    private int slot, previousSlot, enabled;

    public void setSlot(final int slot) {
        setEnabled();
        this.slot = slot;
    }

    public void setSlotDelayed(final int slot, boolean force) {
        if (Math.random() * Math.random() > 0.25 || force) {
            setSlot(slot);
        } else {
            setEnabled();
        }
    }

    private void setEnabled() {
        if (!wasEnabled()) previousSlot = getItemIndex();
        enabled = mc.player.ticksExisted;
    }

    @EventLink(value = Priority.VERY_LOW)
    private final Listener<PreUpdateEvent> onPreUpdate = event -> {
        final InventoryPlayer inventory = mc.player.inventory;

        if (this.isEnabled() && slot >= 0 && slot < 9) {
            inventory.currentItem = slot;
        } else if (wasEnabled()) {
            inventory.currentItem = previousSlot;
        }
    };

    private boolean isEnabled() {
        return enabled == mc.player.ticksExisted;
    }

    private boolean wasEnabled() {
        return enabled == mc.player.ticksExisted - 1;
    }

    public ItemStack getItemStack() {
        return (mc.player == null || mc.player.inventoryContainer == null ? null : mc.player.inventoryContainer.getSlot(getItemIndex() + 36).getStack());
    }

    public Item getItem() {
        ItemStack stack = getItemStack();
        return stack == null ? null : stack.getItem();
    }

    public int getItemIndex() {
        return mc.player.inventory.currentItem;
    }
}