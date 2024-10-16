package cc.unknown.component.impl.player;

import static net.minecraft.network.play.client.C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

public final class BadPacketsComponent extends Component {

    private static boolean slot, attack, swing, block, inventory;

    public static boolean bad() {
        return bad(true, true, true, true, true);
    }

    public static boolean bad(final boolean slot, final boolean attack, final boolean swing, final boolean block, final boolean inventory) {
        return (BadPacketsComponent.slot && slot) ||
                (BadPacketsComponent.attack && attack) ||
                (BadPacketsComponent.swing && swing) ||
                (BadPacketsComponent.block && block) ||
                (BadPacketsComponent.inventory && inventory);
    }

    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<PacketEvent> onPacket = event -> {
        final Packet<?> packet = event.getPacket();
        if (event.isSend()) {
	        if (packet instanceof C09PacketHeldItemChange) {
	            slot = true;
	        } else if (packet instanceof C0APacketAnimation) {
	            swing = true;
	        } else if (packet instanceof C02PacketUseEntity) {
	            attack = true;
	        } else if (packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C07PacketPlayerDigging) {
	            block = true;
	        } else if (packet instanceof C0EPacketClickWindow ||
	                (packet instanceof C16PacketClientStatus && ((C16PacketClientStatus) packet).getStatus() == OPEN_INVENTORY_ACHIEVEMENT) ||
	                packet instanceof C0DPacketCloseWindow) {
	            inventory = true;
	        } else if (packet instanceof C03PacketPlayer) {
	            reset();
	        }
        }
    };

    public static void reset() {
        slot = false;
        swing = false;
        attack = false;
        block = false;
        inventory = false;
    }
}
