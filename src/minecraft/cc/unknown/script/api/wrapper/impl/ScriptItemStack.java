package cc.unknown.script.api.wrapper.impl;

import cc.unknown.script.api.wrapper.ScriptWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author Strikeless
 * @since 20.06.2022
 */
public class ScriptItemStack extends ScriptWrapper<ItemStack> {

    public ScriptItemStack(final ItemStack wrapped) {
        super(wrapped);
    }

    public int getAmount() {
        return this.wrapped.getStackSize();
    }

    public int getMaxAmount() {
        return this.wrapped.getMaxStackSize();
    }

    public int getItemId() {
        return this.wrapped == null ? 0 : Item.getIdFromItem(this.wrapped.getItem());
    }

    public ItemStack getWrapped() {
        return this.wrapped;
    }

    public String getName() {
        return this.wrapped.getDisplayName();
    }
}
