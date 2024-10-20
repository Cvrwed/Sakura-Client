package cc.unknown.module.impl.latency;

import java.util.LinkedHashMap;

import cc.unknown.component.impl.player.PingSpoofComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.math.MathUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S00PacketKeepAlive;

/**
 * @author Alan
 * @since 28/05/2022
 */

@ModuleInfo(aliases = "Ping Spoof", description = "Simulates higher latency to the server", category = Category.LATENCY)
public class PingSpoof extends Module {

    private final BoundsNumberValue delay = new BoundsNumberValue("Delay", this, 200, 200, 50, 30000, 1, () -> this.pingOnly.getValue());
    private final BooleanValue pingOnly = new BooleanValue("Ping Only", this, false);
    private final BooleanValue teleports = new BooleanValue("Delay Teleports", this, false, () -> pingOnly.getValue());
    private final BooleanValue velocity = new BooleanValue("Delay Velocity", this, false, () -> pingOnly.getValue());
    private final BooleanValue entities = new BooleanValue("Delay Entity Movements", this, false, () -> pingOnly.getValue());

    private final LinkedHashMap<Packet<?>, Long> packetQueue = new LinkedHashMap<>();
    
    @Override
    public void onDisable() {
    	PingSpoofComponent.dispatch();
    }
    
    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        if (!pingOnly.getValue()) {
            PingSpoofComponent.spoof(
                (int) MathUtil.getRandom(delay.getValue().doubleValue(), delay.getSecondValue().doubleValue()),
                true,
                velocity.getValue(),
                teleports.getValue(),
                entities.getValue()
            );
        }
    };

    @EventLink
    public final Listener<PacketEvent> onPacketReceive = event -> {
        Packet<?> packet = event.getPacket();

        if (event.isReceive()) {
	        if (pingOnly.getValue()) {
	            if (packet instanceof S00PacketKeepAlive) {
	                event.setCancelled();
	                synchronized (packetQueue) {
	                    packetQueue.put(packet, System.currentTimeMillis());
	                }
	            }
	        }
        }
    };
}
