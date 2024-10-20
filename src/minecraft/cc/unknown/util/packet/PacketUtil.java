package cc.unknown.util.packet;

import java.util.Arrays;

import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.script.api.NetworkAPI;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.SlotUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2FPacketSetSlot;

public final class PacketUtil implements Accessor {

	public static void send(final Packet packet) {
		mc.getNetHandler().addToSendQueue(packet);
	}

	public static void sendNoEvent(final Packet packet) {
		mc.getNetHandler().addToSendQueueUnregistered(packet);
	}

	public static void queue(final Packet packet) {
		if (packet == null) {
			System.out.println("Packet is null");
			return;
		}

		if (isClientPacket(packet)) {
			mc.getNetHandler().addToSendQueue(packet);
		} else {
			mc.getNetHandler().addToReceiveQueue(packet);
		}
	}

	public void queueNoEvent(final Packet packet) {
		if (isClientPacket(packet)) {
			mc.getNetHandler().addToSendQueueUnregistered(packet);
		} else {
			mc.getNetHandler().addToReceiveQueueUnregistered(packet);
		}
	}

	public static void receive(final Packet<?> packet) {
		mc.getNetHandler().addToReceiveQueue(packet);
	}

	public static void receiveNoEvent(final Packet<?> packet) {
		mc.getNetHandler().addToReceiveQueueUnregistered(packet);
	}

	public static boolean isServerPacket(final Packet<?> packet) {
		return !isClientPacket(packet);
	}

	public static boolean isClientPacket(final Packet<?> packet) {
		return Arrays.stream(NetworkAPI.serverbound).anyMatch(clazz -> clazz == packet.getClass());
	}

	public static void correctBlockCount(PacketEvent event) { // rewrite
		if (mc.player == null || mc.player.isDead)
			return;

		if (event.getPacket() instanceof S2FPacketSetSlot) {
			final S2FPacketSetSlot wrapper = (S2FPacketSetSlot) event.getPacket();

			if (wrapper.stack() == null) {
				event.setCancelled();
				return;
			}

			try {
				int slot = wrapper.slotID() - 36;
				if (slot < 0 || slot >= mc.player.inventory.mainInventory.length)
					return;

				final ItemStack currentStack = mc.player.inventory.getStackInSlot(slot);
				final Item receivedItem = wrapper.stack().getItem();

				boolean shouldCancel = ((currentStack == null && wrapper.stack().stackSize <= 6
						&& receivedItem instanceof ItemBlock
						&& SlotUtil.blacklist.stream()
								.noneMatch(block -> block.equals(((ItemBlock) receivedItem).getBlock())))
						|| (currentStack != null && Math.abs(currentStack.stackSize - wrapper.stack().stackSize) <= 6));

				if (shouldCancel) {
					event.setCancelled();
				}
			} catch (ArrayIndexOutOfBoundsException exception) {
				exception.printStackTrace();
			}
		}
	}
}
