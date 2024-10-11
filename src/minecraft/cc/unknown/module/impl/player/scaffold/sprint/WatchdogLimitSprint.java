package cc.unknown.module.impl.player.scaffold.sprint;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.PreUpdateEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.util.vector.Vector3d;
import cc.unknown.value.Mode;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.EnumFacing;

public class WatchdogLimitSprint extends Mode<Scaffold> {

    private int ticks;

    public WatchdogLimitSprint(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink(value = Priority.HIGH)
    private final Listener<PreUpdateEvent> preMotionEventListener = event -> {
        RotationComponent.setSmoothed(false);

        if (ticks > 1 && !mc.gameSettings.keyBindJump.isKeyDown()) {
            getParent().offset = getParent().offset.add(0, -1, 0);
        }

        RotationComponent.setRotations(new Vector2f(mc.player.rotationYaw - 180 - 45, 88), 10, MovementFix.SILENT);

        mc.gameSettings.keyBindSprint.setPressed(false);
    };

    @EventLink
    public final Listener<PacketEvent> eventListener = event -> {
	    if (!event.isSend()) return;

        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement) event.getPacket();
            if (!packet.getPosition().equalsVector(new Vector3d(-1, -1, -1)) && EnumFacing.UP.getIndex() != packet.getPlacedBlockDirection()) {
                if (packet.getPosition().getY() < mc.player.posY - 1) {
                    ticks = 0;
                } else {
                    ticks++;
                }
            }
        }
    };

    @Override
    public void onEnable() {
        ticks++;
    }
}