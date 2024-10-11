package cc.unknown.module.impl.movement;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.PostStrafeEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.vector.Vector3d;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(aliases = {"Stuck", "Freeze"}, description = "Makes you get stuck", category = Category.MOVEMENT)
public class Stuck extends Module {
    private Vector3d motion;

    @Override
    public void onEnable() {
        motion = new Vector3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
    }

    @Override
    public void onDisable() {
        mc.player.motionX = motion.x;
        mc.player.motionY = motion.y;
        mc.player.motionZ = motion.z;
    }

    @EventLink
    public final Listener<PostStrafeEvent> onPostStrafe = event -> {
        MoveUtil.stop();
        mc.player.motionY = 0;
    };

    @EventLink
    public final Listener<PacketEvent> onPacketSend = event -> {
        Packet<?> packet = event.getPacket();
	    if (!event.isSend()) return;

        if (packet instanceof C03PacketPlayer) {
            event.setCancelled();
        }
    };
}