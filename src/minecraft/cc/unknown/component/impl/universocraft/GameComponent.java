package cc.unknown.component.impl.universocraft;

import java.util.List;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.MoveUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

public class GameComponent extends Component {
	
    private static boolean joining;
    private static int lobby;
    private static Item item;
    private static int stage;
    private static boolean foundItem;

	@EventLink
	public final Listener<PacketEvent> onPacketReceive = event -> {
		final Packet<?> packet = event.getPacket();

		if (isInGame() && event.isReceive()) {
			if (packet instanceof S08PacketPlayerPosLook)
				this.joining = false;
			if (this.stage == 2 && packet instanceof S2DPacketOpenWindow)
				this.stage = 3;
			if (this.stage >= 3 && packet instanceof S2EPacketCloseWindow)
				this.stage = 0;
		}
	};
	
	@EventLink
	public final Listener<TickEvent> onTick = event -> {
        if (isInGame()) {
            if (mc.currentScreen instanceof GuiChat || MoveUtil.isMoving()) {
                this.joining = false;
                return;
            }

            if (!this.joining)
                return;

            switch (this.stage) {

                case 0:
                    if (!this.foundItem && mc.player.inventoryContainer.getSlot(36).getHasStack()) {
                        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.player.getHeldItem()));
                        this.stage++;
                    }
                    break;
                case 1:
                    if (mc.currentScreen instanceof GuiContainer) {
                        GuiContainer container = (GuiContainer) mc.currentScreen;
                        List<ItemStack> inventory = container.inventorySlots.getInventory();
                        for (int i = 0; i < inventory.size(); i++) {
                            ItemStack slot = inventory.get(i);
                            if (slot != null)
                                if (slot.getItem() == this.item) {
                                    PacketUtil.send(new C0EPacketClickWindow(container.inventorySlots.windowId, i, 0, 0, slot, (short) 1));
                                    this.stage++;
                                    break;
                                }
                        }
                    }
                    break;
                case 3:
                    if (mc.currentScreen instanceof GuiContainer) {
                        GuiContainer container = (GuiContainer) mc.currentScreen;
                        List<ItemStack> inventory = container.inventorySlots.getInventory();
                        for (int i = 0; i < inventory.size(); i++) {
                            ItemStack slot = inventory.get(i);
                            if (slot != null)
                                if (slot.stackSize == this.lobby) {
                                    PacketUtil.send(new C0EPacketClickWindow(container.inventorySlots.windowId, i, 0, 0, slot, (short) 1));
                                    this.stage++;
                                    break;
                                }
                        }
                    }
                    break;
                case 4:
                    if (mc.player.ticksExisted % 11 == 0)
                        this.stage = 3;
                    break;
            }
        }
	};
	
    public static void init(Item name, int lobbyNumber) {
        joining = true;
        item = name;
        lobby = lobbyNumber;
        stage = 0;
        foundItem = false;
    }
}
