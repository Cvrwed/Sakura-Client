package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.MathHelper;

@ModuleInfo(aliases = "Hurt Camera", description = "Modifies the hurt camera animation", category = Category.VISUALS)
public final class HurtCamera extends Module {

    public final NumberValue intensity = new NumberValue("Intensity", this, 1, 0, 1, 0.1);

    @EventLink
    public final Listener<PacketEvent> onPacketReceiveEvent = event -> {
        final Packet<?> packet = event.getPacket();
		if (!event.isReceive()) return;

        if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity wrapper = ((S12PacketEntityVelocity) packet);

            if (wrapper.getEntityID() == mc.player.getEntityId()) {
                final double velocityX = wrapper.motionX / 8000.0D;
                final double velocityZ = wrapper.motionZ / 8000.0D;

                mc.player.attackedAtYaw = (float) (MathHelper.atan2(velocityX, velocityZ) * 180.0D / Math.PI * 2 - (double) mc.player.rotationYaw);
            }
        }
    };

    @Override
    public void onDisable() {
        mc.player.attackedAtYaw = 0;
    }
}