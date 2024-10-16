package cc.unknown.module.impl.world;

import cc.unknown.component.impl.player.Slot;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.item.ItemBlock;

/**
 * @author Alan
 * @since 29/01/2021
 */

@ModuleInfo(aliases = "Fast Place", description = "Makes the place delay shorter", category = Category.WORLD)
public class FastPlace extends Module {

	private final NumberValue delay = new NumberValue("Delay", this, 0, 0, 3, 1);

	@EventLink
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		if (getComponent(Slot.class).getItemStack() != null
				&& getComponent(Slot.class).getItemStack().getItem() instanceof ItemBlock) {
			mc.rightClickDelayTimer = Math.min(mc.rightClickDelayTimer, delay.getValue().intValue());

		}

	};
}