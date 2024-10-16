package cc.unknown.module.impl.world;

import cc.unknown.component.impl.player.Slot;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.item.ItemBlock;

/**
 * @author Alan
 * @since 29/01/2021
 */

@ModuleInfo(aliases = "Safe Walk", description = "Stops you from falling off the edge", category = Category.WORLD)
public class SafeWalk extends Module {

    private final BooleanValue blocksOnly = new BooleanValue("Blocks Only", this, false);
    private final BooleanValue backwardsOnly = new BooleanValue("Backwards Only", this, false);

    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        mc.player.safeWalk = mc.player.onGround && (!mc.gameSettings.keyBindForward.isKeyDown() || !backwardsOnly.getValue()) &&
                ((getComponent(Slot.class).getItemStack() != null && getComponent(Slot.class).getItemStack().getItem() instanceof ItemBlock) ||
                        !this.blocksOnly.getValue());
    };
}