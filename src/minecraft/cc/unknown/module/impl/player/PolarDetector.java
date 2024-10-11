package cc.unknown.module.impl.player;

import cc.unknown.component.impl.render.NotificationComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.chat.ChatUtil;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

@ModuleInfo(aliases = "Polar Detector", description = "Polar status", category = Category.PLAYER)
public class PolarDetector extends Module {
	private boolean transaction = false;

	@Override
	public void onEnable() {
		NotificationComponent.post("Polar Detector", "Join a game and this module will notify you of polars status");
	}

	@EventLink
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			if (mc.player.ticksExisted == 30) {
				ChatUtil.display(transaction ? "Polar is enabled" : "Polar is disabled");
			}
		}
	};

	@EventLink
	public final Listener<PacketEvent> onPacketSend = event -> {
	    if (!event.isSend()) return;

		if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
			transaction = true;
		}
	};

	@EventLink
	public final Listener<WorldChangeEvent> onWorldChange = event -> {
		transaction = false;
	};
}