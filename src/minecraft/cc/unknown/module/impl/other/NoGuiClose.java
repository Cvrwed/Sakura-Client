package cc.unknown.module.impl.other;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

/**
 * @author Alan Jr.
 * @since 9/17/2022
 */

@ModuleInfo(aliases = "No Gui Close", category = Category.OTHER, description = "Prevents servers from closing opened GUIs")
public final class NoGuiClose extends Module {
    private final BooleanValue chatonly = new BooleanValue("Chat Only", this, false);

    @EventLink
    public final Listener<PacketEvent> onPacketReceive = event -> {
        final Packet<?> packet = event.getPacket();
	    if (!event.isReceive()) return;

        if (event.getPacket() instanceof S2EPacketCloseWindow && (mc.currentScreen instanceof GuiChat || !chatonly.getValue())) {
            event.setCancelled();
        }
    };
}

