package cc.unknown.script.api;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.script.api.wrapper.impl.ScriptEntity;
import cc.unknown.script.api.wrapper.impl.ScriptItemStack;
import cc.unknown.script.api.wrapper.impl.vector.ScriptVector3d;
import cc.unknown.util.packet.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class PacketAPI extends API {

    public void sendKeepAlive(final int key) {
        PacketUtil.send(new C00PacketKeepAlive(key));
    }

    public void sendMessage(final String message) {
        PacketUtil.send(new C01PacketChatMessage(message));
    }

    public void sendUseEntity(final ScriptEntity entity, final String action) {
        sendUseEntity(entity.getEntityId(), action);
    }

    public void sendUseEntity(final int entityID, final String action) {
        PacketUtil.send(new C02PacketUseEntity(Minecraft.getMinecraft().world.getEntityByID(entityID),
                C02PacketUseEntity.Action.valueOf(action)));
    }

    public void sendUseEntity(final int entityID, ScriptVector3d vector) {
        PacketUtil.send(new C02PacketUseEntity(Minecraft.getMinecraft().world.getEntityByID(entityID),
                new Vec3(vector.getX(), vector.getY(), vector.getZ())));
    }

    public void sendUseEntity(final ScriptEntity entity, ScriptVector3d vector) {
        sendUseEntity(entity.getEntityId(), vector);
    }

    public void sendPosition(boolean ground) {
        PacketUtil.send(new C03PacketPlayer(ground));
    }

    public void sendPosition(double x, double y, double z, boolean ground) {
        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
    }

    public void sendPosition(double x, double y, double z, float yaw, float pitch, boolean ground) {
        PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, pitch, ground));
    }

    public void sendDigging(String status, ScriptVector3d vector3d, String facing) {
        PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.valueOf(status),
                new BlockPos(vector3d.getX(), vector3d.getY(), vector3d.getZ()), EnumFacing.valueOf(facing)));
    }

    public void sendPlacement() {
        PacketUtil.send(new C08PacketPlayerBlockPlacement(Sakura.instance.getComponentManager().get(Slot.class).getItemStack()));
    }

    public void sendPlacement(ScriptVector3d vector3d, int direction, float x, float y, float z) {
        PacketUtil.send(new C08PacketPlayerBlockPlacement(new BlockPos(vector3d.getX(), vector3d.getY(), vector3d.getZ()),
                direction, Sakura.instance.getComponentManager().get(Slot.class).getItemStack(), x, y, z));
    }

    public void sendPlacement(ScriptVector3d vector3d, int direction, ScriptItemStack itemStack, float x, float y, float z) {
        PacketUtil.send(new C08PacketPlayerBlockPlacement(new BlockPos(vector3d.getX(), vector3d.getY(), vector3d.getZ()),
                direction, itemStack.getWrapped(), x, y, z));
    }

    public void sendChangeItem(int slot) {
        PacketUtil.send(new C09PacketHeldItemChange(slot));
    }

    public void sendAnimation(int entityID, String animation) {
        PacketUtil.send(new C0APacketAnimation());
    }

    public void sendEntityAction(int entityID, String action) {
        PacketUtil.send(new C0BPacketEntityAction(Minecraft.getMinecraft().world.getEntityByID(entityID),
                C0BPacketEntityAction.Action.valueOf(action)));
    }

    public void sendInput(final float strafeSpeed, final float forwardSpeed, final boolean jumping, final boolean sneaking) {
        PacketUtil.send(new C0CPacketInput(strafeSpeed, forwardSpeed, jumping, sneaking));
    }

    public void sendCloseWindow(int windowId) {
        PacketUtil.send(new C0DPacketCloseWindow(windowId));
    }

    public void sendCloseWindow() {
        PacketUtil.send(new C0DPacketCloseWindow(Minecraft.getMinecraft().player.openContainer.windowId));
    }

    public void sendEnchantItem(int windowId, int enchantment) {
        PacketUtil.send(new C11PacketEnchantItem(windowId, enchantment));
    }

    public void sendEnchantItem(int enchantment) {
        PacketUtil.send(new C11PacketEnchantItem(Minecraft.getMinecraft().player.openContainer.windowId, enchantment));
    }

    public void sendTransaction(int windowId, short actionNumber, boolean accepted) {
        PacketUtil.send(new C0FPacketConfirmTransaction(windowId, actionNumber, accepted));
    }

    public void sendAbilities() {
        PacketUtil.send(new C13PacketPlayerAbilities(Minecraft.getMinecraft().player.capabilities));
    }

    public void sendAbilities(boolean flying, boolean allowFlying, boolean creativeMode) {
        PlayerCapabilities capabilities = new PlayerCapabilities();
        capabilities.isFlying = flying;
        capabilities.allowFlying = allowFlying;
        capabilities.isCreativeMode = creativeMode;
        PacketUtil.send(new C13PacketPlayerAbilities(capabilities));
    }

    public void sendTabComplete(String message) {
        PacketUtil.send(new C14PacketTabComplete(message));
    }

    public void sendTabComplete(String message, ScriptVector3d vector3d) {
        PacketUtil.send(new C14PacketTabComplete(message, new BlockPos(vector3d.getX(), vector3d.getY(), vector3d.getZ())));
    }

    public void sendStatus(String status) {
        PacketUtil.send(new C16PacketClientStatus(C16PacketClientStatus.EnumState.valueOf(status)));
    }

    public void sendSettings() {
        PacketUtil.send(new C15PacketClientSettings());
    }
}
