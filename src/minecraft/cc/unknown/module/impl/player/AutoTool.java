package cc.unknown.module.impl.player;

import cc.unknown.component.impl.player.BadPacketsComponent;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.BlockDamageEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.SlotUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.util.BlockPos;

/**
 * @author Alan (made good code)
 * @since 24/06/2023
 */

@ModuleInfo(aliases = {"Auto Tool"}, description = "Switches to the most efficient tool when breaking a block", category = Category.PLAYER)
public class AutoTool extends Module {

    private int slot, lastSlot = -1;
    private int blockBreak, ran;
    private BlockPos blockPos;
    
    @EventLink(Priority.VERY_HIGH)
    public final Listener<BlockDamageEvent> onBlockDamage = event -> {
        if (event.getPlayer() != mc.player || mc.player.getDistanceSq(event.getBlockPos().getX(), event.getBlockPos().getY(),event.getBlockPos().getZ()) > 5 * 5) return;
        blockBreak = 15;
        blockPos = event.getBlockPos();
        this.update();
    };

    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        this.update();
    };

    public void update() {
        if (mc.objectMouseOver == null) {
            blockBreak = -1;
            return;
        }

        switch (mc.objectMouseOver.typeOfHit) {
            case BLOCK:
                if (blockPos != null && blockBreak > 0) {
                    slot = SlotUtil.findTool(blockPos);
                } else {
                    slot = -1;
                }
                break;

            default:
                slot = -1;
                break;
        }

        if (lastSlot != -1) {
            getComponent(Slot.class).setSlot(lastSlot);
        } else if (slot != -1) {
            getComponent(Slot.class).setSlot(slot);
        }

        lastSlot = slot;
        blockBreak--;

        if (!BadPacketsComponent.bad(false, false, true, false, false)) {
            blockBreak = -1;
        }
    }

}