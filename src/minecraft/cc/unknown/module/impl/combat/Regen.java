package cc.unknown.module.impl.combat;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(aliases = "Regen", description = "Makes you regenerate health faster", category = Category.COMBAT)
public class Regen extends Module {

	private final NumberValue delay = new NumberValue("Delay", this, 500, 0, 1000, 50);
	private final NumberValue health = new NumberValue("Minimum Health", this, 15, 1, 20, 1);
	private final NumberValue packets = new NumberValue("Packets", this, 20, 1, 100, 1);
	private final BooleanValue onGround = new BooleanValue("On Ground", this, false);

	private final StopWatch stopWatch = new StopWatch();

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mc.player.getHealth() >= health.getValue().floatValue()) return;
		if (onGround.getValue() && !mc.player.onGround) return;

		for (int i = 0; i < packets.getValue().intValue(); i++) {
			if (stopWatch.reached(delay.getValue().longValue())) {
				PacketUtil.send(new C03PacketPlayer(true));
				stopWatch.reset();
			}		
		}
	};
}
