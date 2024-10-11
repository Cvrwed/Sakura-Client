package cc.unknown.module.impl.player;

import cc.unknown.component.impl.player.GUIDetectionComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.math.MathUtil;
import cc.unknown.util.player.ItemUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;


@ModuleInfo(aliases = {"Stealer"}, description = "Steals items from chests for you", category = Category.PLAYER)
public class Stealer extends Module {

    private final BoundsNumberValue delay = new BoundsNumberValue("Delay", this, 100, 150, 0, 500, 50);
    private final BooleanValue ignoreTrash = new BooleanValue("Ignore Trash", this, true);
    private final StopWatch stopwatch = new StopWatch();
    private long nextClick;
    private int lastClick;
    private int lastSteal;
    private int open;
    private boolean finished;

    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdateEvent = event -> {
        if (mc.currentScreen instanceof GuiChest) {
            open++;
            this.finished = false;
            final ContainerChest container = (ContainerChest) mc.player.openContainer;

            if (GUIDetectionComponent.inGUI() || !this.stopwatch.finished(this.nextClick)) {
                return;
            }

            this.lastSteal++;

            for (int i = 0; i < container.inventorySlots.size(); i++) {
                final ItemStack stack = container.getLowerChestInventory().getStackInSlot(i);

                if (stack == null || this.lastSteal <= 1) {
                    continue;
                }

                if (this.ignoreTrash.getValue() && !ItemUtil.useful(stack)) {
                    continue;
                }

                this.nextClick = Math.round(MathUtil.getRandom(this.delay.getValue().intValue(), this.delay.getSecondValue().intValue()));
                mc.playerController.windowClick(container.windowId, i, 0, 1, mc.player);
                this.stopwatch.reset();
                
                this.lastClick = 0;
                if (this.nextClick > 0) return;
            }

            this.lastClick++;

            if (this.lastClick > 1 && open > 2 + (2 * Math.random())) {
                mc.player.closeScreen();
                this.finished = true;
            }
        } else {
            this.lastClick = 0;
            this.open = 0;
            this.lastSteal = 0;
        }
    };

    public boolean isFinished() {
        return this.finished;
    }
}