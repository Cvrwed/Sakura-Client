package cc.unknown.module.impl.world.scaffold.sprint;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.value.Mode;

public class BypassSprint extends Mode<Scaffold> {

    public BypassSprint(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			mc.player.setSprinting(false);
		}
    };
}
