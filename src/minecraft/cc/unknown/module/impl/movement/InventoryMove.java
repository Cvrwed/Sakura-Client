package cc.unknown.module.impl.movement;

import java.util.concurrent.ConcurrentLinkedQueue;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

/**
 * @author Alan
 * @since 20/10/2021
 */

@ModuleInfo(aliases = { "Inventory Move",
		"Inv Move" }, description = "Allows you to move whilst in GUIs", category = Category.MOVEMENT)
public class InventoryMove extends Module {

	private final ModeValue mode = new ModeValue("Bypass Mode", this).add(new SubMode("Normal"))
			.add(new SubMode("Buffer Abuse")).add(new SubMode("Cancel")).add(new SubMode("Watchdog"))
			.setDefault("Normal");

	private final NumberValue clicksSetting = new NumberValue("Clicks", this, 3, 2, 10, 1,
			() -> !mode.is("Buffer Abuse"));
	private final NumberValue amount = new NumberValue("Amount", this, 5, 1, 10, 1, () -> !mode.is("Buffer Abuse"));

	private final KeyBinding[] AFFECTED_BINDINGS = new KeyBinding[] { mc.gameSettings.keyBindForward,
			mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft,
			mc.gameSettings.keyBindJump };
	private final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
	private boolean done, sent;
	private int clicks;

	@EventLink
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		if (!mode.is("watchdog")) {
			if (mc.currentScreen instanceof GuiChat || mc.currentScreen == this.getClickGUI()) {
				return;
			}

			for (final KeyBinding bind : AFFECTED_BINDINGS) {
				bind.setPressed(GameSettings.isKeyDown(bind));
			}
		}
	};

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mode.is("buffer abuse")) {
			if (this.canAbuse()) {
				if (!this.sent) {
					if (!this.done) {
						this.done = true;
					} else {
						for (int i = 0; i < this.amount.getValue().intValue(); i++) {
							PacketUtil.sendNoEvent(new C0EPacketClickWindow());
						}
						packets.forEach(PacketUtil::sendNoEvent);
						packets.clear();
						this.sent = true;
					}
				}
			} else {
				this.done = false;
				this.sent = false;
			}
		}

		if (mode.is("watchdog")) {
			if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiChest
					|| mc.currentScreen == this.getClickGUI() || mc.currentScreen instanceof GuiInventory) {
				MoveUtil.stop();
			}
		}

	};

	@EventLink
	public final Listener<PreStrafeEvent> onStrafe = event -> {
		if (mode.is("buffer abuse")) {
			if (this.canAbuse() && !this.sent) {
				event.setCancelled();
			}
		}
	};

	@EventLink
	public final Listener<JumpEvent> onJump = event -> {
		if (mode.is("buffer abuse")) {
			if (this.canAbuse() && !this.sent) {
				event.setCancelled();
			}
		}
	};

	@EventLink
	public final Listener<PacketEvent> onPacketSend = event -> {
		final Packet<?> p = event.getPacket();
		if (!event.isSend())
			return;

		if (mode.is("buffer abuse")) {
			if (p instanceof C0EPacketClickWindow) {
				if (this.canAbuse() && !this.sent) {
					event.setCancelled();
					packets.add(p);
					return;
				}
				this.clicks++;
			}
		}
		if (mode.is("cancel")) {
			if (p instanceof C16PacketClientStatus) {
				final C16PacketClientStatus wrapper = (C16PacketClientStatus) p;

				if (wrapper.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
					event.setCancelled();
				}
			}

			if (p instanceof C0BPacketEntityAction) {
				final C0BPacketEntityAction wrapper = (C0BPacketEntityAction) p;

				if (wrapper.getAction() == C0BPacketEntityAction.Action.OPEN_INVENTORY) {
					event.setCancelled();
				}
			}

			if (p instanceof C0DPacketCloseWindow) {
				event.setCancelled();
			}
		}
	};

	@EventLink
	public final Listener<WorldChangeEvent> onWorldChange = event -> {
		if (mode.is("buffer abuse")) {
			packets.clear();
		}
	};

	private boolean canAbuse() {
		return clicks > 0 && clicks % this.clicksSetting.getValue().intValue() == 0;
	}
}
