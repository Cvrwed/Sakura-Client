package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.EntityLivingBase;

@ModuleInfo(aliases = "No Click Delay", description = "Removes the 1.8 click delay when missing an attack", category = Category.PLAYER)
public class NoClickDelay extends Module {

	@EventLink
	public final Listener<TickEvent> onTick = event -> {
		if (!isInGame()) return;
		mc.leftClickCounter = 0;
	};
}