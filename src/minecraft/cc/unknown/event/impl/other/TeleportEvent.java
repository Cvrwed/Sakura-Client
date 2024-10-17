package cc.unknown.event.impl.other;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.netty.ScriptTeleportEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.play.client.C03PacketPlayer;

@AllArgsConstructor
@Getter
@Setter
public final class TeleportEvent extends CancellableEvent {
    private C03PacketPlayer response;
    private double posX;
    private double posY;
    private double posZ;
    private float yaw;
    private float pitch;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptTeleportEvent(this);
    }
}