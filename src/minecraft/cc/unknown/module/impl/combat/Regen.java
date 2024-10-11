package cc.unknown.module.impl.combat;

import cc.unknown.component.impl.player.Slot;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

@ModuleInfo(aliases = "Regen", description = "Makes you regenerate health faster", category = Category.COMBAT)
public class Regen extends Module {

	private final NumberValue delay = new NumberValue("Delay", this, 500, 0, 1000, 50);
	private final NumberValue health = new NumberValue("Minimum Health", this, 15, 1, 20, 1);
	private final NumberValue packets = new NumberValue("Packets", this, 20, 1, 100, 1);
	private final BooleanValue onGround = new BooleanValue("On Ground", this, false);

	private final StopWatch stopWatch = new StopWatch();

	@EventLink
	public final Listener<MotionEvent> onPreMotionEvent = event -> {
		if (mc.player.getHealth() >= health.getValue().floatValue()) return;
		if (onGround.getValue() && !mc.player.onGround) return;

		if (event.isPre()) {
			int foodSlot = getFoodSlotInHotbar();
			if (foodSlot != 0) {
				swap(foodSlot, getComponent(Slot.class).getItemIndex());
				PacketUtil.send(new C08PacketPlayerBlockPlacement(getComponent(Slot.class).getItemStack()));
				
				for (int i = 0; i < packets.getValue().intValue(); i++) {
					if (stopWatch.reached(delay.getValue().longValue())) {
                        PacketUtil.send(new C03PacketPlayer(true));
						stopWatch.reset();
					}
				}
				
				mc.player.stopUsingItem();
				swap(foodSlot, getComponent(Slot.class).getItemIndex());
				mc.playerController.syncCurrentPlayItem();
			}
		}
	};

	private int getFoodSlotInHotbar() {
		try {
		    for (int i = 9; i < 45; i++) {
		      if ((mc.player.inventoryContainer.getSlot(i) != null) && (mc.player.inventoryContainer.getSlot(i).getStack() != null) && (mc.player.inventoryContainer.getSlot(i).getStack().getItem() != null) && ((this.mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemFood))) {
		        return i;
		      }
		    }
		} catch (Exception e) {}
	    return -1;
	}

	protected void swap(int one, int two) {
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, one, two, 2, mc.player);
	}
}
