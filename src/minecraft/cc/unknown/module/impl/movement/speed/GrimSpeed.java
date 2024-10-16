package cc.unknown.module.impl.movement.speed;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.movement.Speed;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;

public class GrimSpeed extends Mode<Speed> {

    public GrimSpeed(String name, Speed parent) {
        super(name, parent);
    }

    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<PreStrafeEvent> strafe = event -> mc.world.playerEntities.stream().filter(entityPlayer -> entityPlayer != mc.player && mc.player.getEntityBoundingBox().expand(1, 1, 1).intersectsWith(entityPlayer.getEntityBoundingBox())).forEach(entityPlayer -> MoveUtil.moveFlying(0.08));
}
