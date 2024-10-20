package cc.unknown.module.impl.other;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatFormatting;

@ModuleInfo(aliases = "Murder Mystery", description = "Detects murderers in Murder Mystery", category = Category.OTHER)
public final class MurderMystery extends Module {

	private EntityPlayer murderer;

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mc.player.ticksExisted % 2 == 0 || this.murderer != null) {
			return;
		}

		for (EntityPlayer player : mc.world.playerEntities) {
			if (player.getHeldItem() != null) {
				ChatFormatting yellow = ChatFormatting.YELLOW;
				ChatFormatting red = ChatFormatting.RED;
				String item = player.getHeldItem().getDisplayName();
				if (item.contains("Knife") || item.contains("Cuchillo")) {
					ChatUtil.display(yellow + "[" + red + "!" + yellow + " ] " + red + PlayerUtil.name(player) + ChatFormatting.RESET + " es el asesino.");
					this.murderer = player;
				}
			}
		}

	};

	@EventLink
	public final Listener<WorldChangeEvent> onWorldChange = event -> this.murderer = null;
}
