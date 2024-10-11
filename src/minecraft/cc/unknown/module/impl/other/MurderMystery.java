package cc.unknown.module.impl.other;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "Murder Mystery", description = "Detects murderers in Murder Mystery", category = Category.OTHER)
public final class MurderMystery extends Module {

	private EntityPlayer murderer;

	@EventLink
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			if (mc.player.ticksExisted % 2 == 0 || this.murderer != null) {
				return;
			}

			for (EntityPlayer player : mc.world.playerEntities) {
				if (player.getHeldItem() != null) {
					if (player.getHeldItem().getDisplayName().contains("Knife")) {
						ChatUtil.display(PlayerUtil.name(player) + " is The Murderer.");
						this.murderer = player;
					}
				}
			}
		}
	};

	@EventLink
	public final Listener<WorldChangeEvent> onWorldChange = event -> this.murderer = null;
}
