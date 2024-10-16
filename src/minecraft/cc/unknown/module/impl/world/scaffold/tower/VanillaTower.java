package cc.unknown.module.impl.world.scaffold.tower;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;

public class VanillaTower extends Mode<Scaffold> {

	public VanillaTower(String name, Scaffold parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mc.gameSettings.keyBindJump.isKeyDown() && PlayerUtil.blockNear(2)) {
			mc.player.motionY = 0.42F;

		}
	};
}
