package cc.unknown.module.impl.player.antivoid;

import cc.unknown.component.impl.player.BlinkComponent;
import cc.unknown.component.impl.player.FallDistanceComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.impl.player.AntiVoid;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.vector.Vector3d;
import cc.unknown.value.Mode;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

public class WatchdogAntiVoid extends Mode<AntiVoid> {

    private Vector3d position, motion;

    private boolean wasVoid, setBack;
    private int overVoidTicks;

    public WatchdogAntiVoid(String name, AntiVoid parent) {
        super(name, parent);
    }

    private final NumberValue distance = new NumberValue("Distance", this, 5, 0, 10, 1);

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        if (mc.player.ticksExisted <= 75) return;

        boolean overVoid = !mc.player.onGround && !PlayerUtil.isBlockUnder();

        if (overVoid) {
            overVoidTicks++;
        } else if (mc.player.onGround) {
            overVoidTicks = 0;
        }

        if (overVoid && position != null && motion != null && overVoidTicks < 30 + distance.getValue().doubleValue() * 20) {
            if (!setBack) {
                wasVoid = true;

                BlinkComponent.blinking = true;
                BlinkComponent.setExempt(C0FPacketConfirmTransaction.class, C00PacketKeepAlive.class, C01PacketChatMessage.class);

                if (FallDistanceComponent.distance > distance.getValue().doubleValue() || setBack) {
                    PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(position.x, position.y - 0.1 - Math.random(), position.z, false));

                    BlinkComponent.packets.clear();

                    FallDistanceComponent.distance = 0;

                    setBack = true;
                }
            } else {
                BlinkComponent.blinking = false;
            }
        } else {

            setBack = false;

            if (wasVoid) {
                BlinkComponent.blinking = false;
                wasVoid = false;
            }

            motion = new Vector3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
            position = new Vector3d(mc.player.posX, mc.player.posY, mc.player.posZ);
        }
    };
}


