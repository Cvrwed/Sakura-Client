package cc.unknown.script.api.wrapper.impl;

import cc.unknown.script.api.wrapper.ScriptWrapper;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author Strikeless
 * @since 20.06.2022
 */
public class ScriptInventory extends ScriptWrapper<InventoryPlayer> {

    public ScriptInventory(InventoryPlayer wrapped) {
        super(wrapped);
    }

    public ScriptItemStack getItemStackInSlot(int slot) {
        return new ScriptItemStack(this.wrapped.getStackInSlot(slot));
    }

    private int slot(final int slot) {
        if (slot >= 36) {
            return 8 - (slot - 36);
        }

        if (slot < 9) {
            return slot + 36;
        }

        return slot;
    }

    public ScriptItemStack getHeldItem() {
        return new ScriptItemStack(this.wrapped.getCurrentItem());
    }
}
