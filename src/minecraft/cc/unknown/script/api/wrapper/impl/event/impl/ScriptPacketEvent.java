package cc.unknown.script.api.wrapper.impl.event.impl;

import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.script.api.wrapper.impl.event.CancellableScriptEvent;

/**
 * @author Alan
 * @since 10/07/2023
 */
public class ScriptPacketEvent extends CancellableScriptEvent<PacketEvent> {

    public ScriptPacketEvent(final PacketEvent wrappedEvent) {
        super(wrappedEvent);
    }
    
    public boolean isSend() {
    	return this.wrapped.isSend();
    }
    
    public boolean isReceive() {
    	return this.wrapped.isReceive();
    }

    @Override
    public String getHandlerName() {
        return "onPacket";
    }
}
