package cc.unknown.module.impl.movement;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;

@ModuleInfo(aliases = "No Jump Delay", description = "Remove jump delay", category = Category.MOVEMENT)
public class NoJumpDelay extends Module {

	@EventLink
	public final Listener<TickEvent> onTick = event -> {
		if (!isInGame()) return;
		mc.player.jumpTicks = 0;
	};
}