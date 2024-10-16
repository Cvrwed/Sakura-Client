package cc.unknown.module.impl.world.scaffold.sprint;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.value.Mode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

public class MatrixSprint extends Mode<Scaffold> {
    private int time;
    private boolean ignore;

    public MatrixSprint(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        time++;

        mc.gameSettings.keyBindSneak.setPressed(time >= 4);
    };

    @EventLink
    public final Listener<PacketEvent> onPacketSend = event -> {
        final Packet<?> p = event.getPacket();
	    if (!event.isSend()) return;

        if (p instanceof C08PacketPlayerBlockPlacement) {
            final C08PacketPlayerBlockPlacement wrapper = (C08PacketPlayerBlockPlacement) p;

            if (wrapper.getPlacedBlockDirection() != 255) {
                time = 0;
            }
        }
    };

    @EventLink(value = Priority.MEDIUM)
    public final Listener<MoveInputEvent> onMove = event -> {
        event.setSneakSlowDownMultiplier(0.5);
        mc.gameSettings.keyBindSprint.setPressed(false);
        mc.player.setSprinting(false);
    };
}
