package cc.unknown.event.impl.netty;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.netty.ScriptPacketEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

@AllArgsConstructor
public class PacketEvent extends CancellableEvent {
    @Getter @Setter private Packet<?> packet;
    private NetworkManager networkManager;
    private PacketDirection packetDirection;
    
    public boolean isSend() {
    	return packetDirection == PacketDirection.Send;
    }
    
    public boolean isReceive() {
    	return packetDirection == PacketDirection.Receive;
    }
	
	public enum PacketDirection {
		Send, Receive;
	}
	
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptPacketEvent(this);
    }
}
