package cc.unknown.util.packet.process;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.*;

public enum OutgoingPackets {
    C00PacketKeepAlive(C00PacketKeepAlive.class),
    C01PacketChatMessage(C01PacketChatMessage.class),
    C02PacketUseEntity(C02PacketUseEntity.class),
    C03PacketPlayer(C03PacketPlayer.class),
    C04PacketPlayerPosition(C03PacketPlayer.C04PacketPlayerPosition.class),
    C05PacketPlayerLook(C03PacketPlayer.C05PacketPlayerLook.class),
    C06PacketPlayerPosLook(C03PacketPlayer.C06PacketPlayerPosLook.class),
    C07PacketPlayerDigging(C07PacketPlayerDigging.class),
    C08PacketPlayerBlockPlacement(C08PacketPlayerBlockPlacement.class),
    C09PacketHeldItemChange(C09PacketHeldItemChange.class),
    C0APacketAnimation(C0APacketAnimation.class),
    C0BPacketEntityAction(C0BPacketEntityAction.class),
    C0CPacketInput(C0CPacketInput.class),
    C0DPacketCloseWindow(C0DPacketCloseWindow.class),
    C0EPacketClickWindow(C0EPacketClickWindow.class),
    C0FPacketConfirmTransaction(C0FPacketConfirmTransaction.class),
    C10PacketCreativeInventoryAction(C10PacketCreativeInventoryAction.class),
    C11PacketEnchantItem(C11PacketEnchantItem.class),
    C12PacketUpdateSign(C12PacketUpdateSign.class),
    C13PacketPlayerAbilities(C13PacketPlayerAbilities.class),
    C14PacketTabComplete(C14PacketTabComplete.class),
    C15PacketClientSettings(C15PacketClientSettings.class),
    C16PacketClientStatus(C16PacketClientStatus.class),
    C17PacketCustomPayload(C17PacketCustomPayload.class),
    C18PacketSpectate(C18PacketSpectate.class),
    C19PacketResourcePacketStatus(C19PacketResourcePackStatus.class);

    private final Class<? extends Packet<INetHandlerPlayServer>> packetClass;

    OutgoingPackets(Class<? extends Packet<INetHandlerPlayServer>> packetClass) {
        this.packetClass = packetClass;
    }

    public Class<? extends Packet<INetHandlerPlayServer>> getPacketClass() {
        return this.packetClass;
    }

    private static final List<Class<? extends Packet<INetHandlerPlayServer>>> outgoingPackets =
            Arrays.stream(values()).map(OutgoingPackets::getPacketClass).collect(Collectors.toList());

    public static List<Class<? extends Packet<INetHandlerPlayServer>>> getOutgoingPackets() {
        return outgoingPackets;
    }
}