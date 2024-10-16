package cc.unknown.module.impl.world.scaffold.tower;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.value.Mode;

public class PolarTower extends Mode<Scaffold> {
    public PolarTower(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink
    public final Listener<PreStrafeEvent> onStrafe = event -> {
        if (!mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }

        if(mc.player.onGround) {
            mc.player.jump();
            mc.player.motionY = 0.39;
        }
    };
}
