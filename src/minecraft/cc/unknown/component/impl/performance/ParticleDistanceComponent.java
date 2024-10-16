package cc.unknown.component.impl.performance;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2APacketParticles;

public class ParticleDistanceComponent extends Component {

    @EventLink
    public final Listener<PacketEvent> onPacketReceiveEvent = event -> {
        final Packet<?> packet = event.getPacket();

        if (event.isReceive()) {
	        if (packet instanceof S2APacketParticles) {
	            final S2APacketParticles wrapper = ((S2APacketParticles) packet);
	
	            final double distance = mc.player.getDistanceSq(wrapper.getXCoordinate(), wrapper.getYCoordinate(), wrapper.getZCoordinate());
	
	            if (distance >= 36) {
	                event.setCancelled();
	            }
	        }
        }
    };
}