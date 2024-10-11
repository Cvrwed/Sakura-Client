package cc.unknown.component.impl.event;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.AttackEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.util.vector.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class EntityTickComponent extends Component {

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<PacketEvent> onPacketSend = event -> {
        if (mc == null || mc.world == null || event.isCancelled()) return;

        Packet<?> packet = event.getPacket();
        
        if (event.isSend()) {
	        if (packet instanceof C08PacketPlayerBlockPlacement && !((C08PacketPlayerBlockPlacement) packet).getPosition().equalsVector(new Vector3d(-1, -1, -1))) {
	            mc.player.ticksSincePlace = 0;
	        }
        }
        
        if (event.isReceive()) {
            if (packet instanceof S12PacketEntityVelocity) {
                final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;

                Entity entity = mc.world.getEntityByID(wrapper.getEntityID());

                if (entity == null) {
                    return;
                }

                entity.lastVelocityDeltaX = wrapper.motionX / 8000.0D;
                entity.lastVelocityDeltaY = wrapper.motionY / 8000.0D;
                entity.lastVelocityDeltaZ = wrapper.motionZ / 8000.0D;
                entity.ticksSinceVelocity = 0;
                if (wrapper.motionY / 8000.0D > 0.1 && Math.hypot(wrapper.motionZ / 8000.0D, wrapper.motionX / 8000.0D) > 0.2) {
                    entity.ticksSincePlayerVelocity = 0;
                }
            } else if (packet instanceof S08PacketPlayerPosLook) {
                mc.player.ticksSinceTeleport = 0;
            }
        }
    };

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<AttackEvent> onAttack = event -> {
        mc.player.ticksSinceAttack = 0;
    };
}
