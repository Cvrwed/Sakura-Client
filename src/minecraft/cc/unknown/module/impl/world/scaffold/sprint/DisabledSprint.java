package cc.unknown.module.impl.world.scaffold.sprint;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.StrafeEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.value.Mode;

public class DisabledSprint extends Mode<Scaffold> {

    public DisabledSprint(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<StrafeEvent> onPreMotionEvent = event -> {
        mc.gameSettings.keyBindSprint.setPressed(false);
        mc.player.setSprinting(false);
    };
}
