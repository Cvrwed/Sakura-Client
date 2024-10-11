package cc.unknown.util.player;

import cc.unknown.util.Accessor;
import cc.unknown.util.packet.PacketUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

@UtilityClass
public final class DamageUtil implements Accessor {

    public void damagePlayer(final double value) {
        damagePlayer(DamageType.POSITION, value, true, true);
    }

    public void damagePlayer(final DamageType type, final double value, final boolean groundCheck, final boolean hurtTimeCheck) {
        if ((!groundCheck || mc.player.onGround) && (!hurtTimeCheck || mc.player.hurtTime == 0)) {
            final double x = mc.player.posX;
            final double y = mc.player.posY;
            final double z = mc.player.posZ;

            double fallDistanceReq = 3.1;

            if (mc.player.isPotionActive(Potion.jump)) {
                final int amplifier = mc.player.getActivePotionEffect(Potion.jump).getAmplifier();
                fallDistanceReq += (float) (amplifier + 1);
            }

            final int packetCount = (int) Math.ceil(fallDistanceReq / value); // Don't change this unless you know the change won't break the self damage.
            for (int i = 0; i < packetCount; i++) {
                switch (type) {
                    case POSITION_ROTATION: {
                        PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(x, y + value, z, mc.player.rotationYaw, mc.player.rotationPitch, false));
                        PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, mc.player.rotationYaw, mc.player.rotationPitch, false));
                        break;
                    }

                    case POSITION: {
                        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(x, y + value, z, false));
                        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                        break;
                    }
                }
            }
            PacketUtil.send(new C03PacketPlayer(true));
        }
    }

    public void damagePlayer(final DamageType type, final double value, final int packets, final boolean groundCheck, final boolean hurtTimeCheck) {
        if ((!groundCheck || mc.player.onGround) && (!hurtTimeCheck || mc.player.hurtTime == 0)) {
            final double x = mc.player.posX;
            final double y = mc.player.posY;
            final double z = mc.player.posZ;

            for (int i = 0; i < packets; i++) {
                switch (type) {
                    case POSITION_ROTATION: {
                        PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(x, y + value, z, mc.player.rotationYaw, mc.player.rotationPitch, false));
                        PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, mc.player.rotationYaw, mc.player.rotationPitch, false));
                        break;
                    }

                    case POSITION: {
                        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(x, y + value, z, false));
                        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                        break;
                    }
                }
            }
            PacketUtil.send(new C03PacketPlayer(true));
        }
    }

    public enum DamageType {
        POSITION_ROTATION,
        POSITION
    }
}
