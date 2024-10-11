package cc.unknown.module.impl.player.scaffold.sprint;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.JumpEvent;
import cc.unknown.event.impl.motion.PreUpdateEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class WatchdogSlow extends Mode<Scaffold> {

    private int ticks;

    public WatchdogSlow(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<JumpEvent> onJump = event -> {
        mc.player.omniSprint = true;
        mc.player.setSprinting(true);
    };

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<PreUpdateEvent> onPreMotion = event -> {
        mc.gameSettings.keyBindSprint.setPressed(false);
        mc.player.setSprinting(false);
        mc.player.omniSprint = false;

        int value = 1;

        if (mc.player.onGroundTicks >= 2) {
            mc.player.jump();
        }

        if (mc.player.onGroundTicks <= 20) {
            mc.gameSettings.keyBindSneak.setPressed(ticks % value == 0 && PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir);
            mc.player.safeWalk = (ticks - 1) % value != 0;

            if (mc.player.onGround) {
                getParent().placeDelay.setValue(1);
                getParent().placeDelay.setSecondValue(1);
             } else {
                getParent().placeDelay.setValue(0);
                getParent().placeDelay.setSecondValue(0);
            }
        }
    };

    @EventLink
    public final Listener<PacketEvent> eventListener = event -> {
	    if (!event.isSend()) return;

        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement) event.getPacket();

            if (!packet.getPosition().equals(new BlockPos(-1, -1, -1))) {
                ticks++;
            }
        }
    };

    @Override
    public void onDisable() {
        mc.player.capabilities.isFlying = false;
        this.mc.timer.timerSpeed = 1;
    }

}