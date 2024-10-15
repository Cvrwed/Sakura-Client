package cc.unknown.module.impl.world.scaffold.tower;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.value.Mode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class MMCTower extends Mode<Scaffold> {

    public MMCTower(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink
    public final Listener<PacketEvent> onPacketSend = event -> {
        final Packet<?> packet = event.getPacket();

	    if (!event.isSend()) return;

        if (mc.gameSettings.keyBindJump.isKeyDown() && packet instanceof C08PacketPlayerBlockPlacement) {
            final C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement = ((C08PacketPlayerBlockPlacement) packet);

            if (c08PacketPlayerBlockPlacement.getPosition().equals(new BlockPos(mc.player.posX, mc.player.posY - 1.4, mc.player.posZ))) {
                mc.gameSettings.keyBindSprint.setPressed(false);
                mc.player.setSprinting(false);
                mc.player.motionY = 0.42F;
            }
        }
    };
}
