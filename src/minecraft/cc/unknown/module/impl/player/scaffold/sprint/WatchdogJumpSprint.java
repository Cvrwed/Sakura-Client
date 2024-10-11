package cc.unknown.module.impl.player.scaffold.sprint;

import cc.unknown.component.impl.player.Slot;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.PreUpdateEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;
import net.minecraft.block.BlockAir;

public class WatchdogJumpSprint extends Mode<Scaffold> {

    public WatchdogJumpSprint(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink(value = Priority.LOW)
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        boolean start = mc.player.lastGroundY == getParent().startY;

        if (getComponent(Slot.class).getItemStack() != null &&
                mc.player.posY > getParent().startY &&
                mc.player.posY + MoveUtil.predictedMotion(mc.player.motionY, 3) <
                        getParent().startY + 1) {
            if (getComponent(Slot.class).getItemStack().realStackSize > 0) {
                mc.rightClickMouse();
            }
        }

        if (!start && mc.player.onGround) {
            mc.player.jump();
        }

        mc.player.omniSprint = MoveUtil.isMoving();
    };
}
