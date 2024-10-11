package cc.unknown.component.impl.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.ServerJoinEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.time.StopWatch;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;

public final class BlinkComponent extends Component {

    public static final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    public static boolean blinking, dispatch;
    public static ArrayList<Class<?>> exemptedPackets = new ArrayList<>();
    public static StopWatch exemptionWatch = new StopWatch();

    public static void setExempt(Class<?>... packets) {
        exemptedPackets = new ArrayList<>(Arrays.asList(packets));
        exemptionWatch.reset();
    }

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<PacketEvent> onPacket = event -> {
    	if (event.isSend()) {
	        if (mc.player == null) {
	            packets.clear();
	            exemptedPackets.clear();
	            return;
	        }
	
	        if (mc.player.isDead || mc.isSingleplayer() || !mc.getNetHandler().doneLoadingTerrain) {
	            packets.forEach(PacketUtil::sendNoEvent);
	            packets.clear();
	            blinking = false;
	            exemptedPackets.clear();
	            return;
	        }
	
	        final Packet<?> packet = event.getPacket();
	
	        if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart ||
	                packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing ||
	                packet instanceof C01PacketEncryptionResponse) {
	            return;
	        }
	
	        if (blinking && !dispatch) {
	            if (exemptionWatch.finished(100)) {
	                exemptionWatch.reset();
	                exemptedPackets.clear();
	            }
	
	            if (!event.isCancelled() && exemptedPackets.stream().noneMatch(packetClass ->
	                    packetClass == packet.getClass())) {
	                packets.add(packet);
	                event.setCancelled();
	            }
	        } else if (packet instanceof C03PacketPlayer) {
	            packets.forEach(PacketUtil::sendNoEvent);
	            packets.clear();
	            dispatch = false;
	        }
    	}
    };

    public static void dispatch() {
        dispatch = true;
    }

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        packets.clear();
        BlinkComponent.blinking = false;
    };

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<ServerJoinEvent> onServerJoin = event -> {
        packets.clear();
        BlinkComponent.blinking = false;
    };

}
