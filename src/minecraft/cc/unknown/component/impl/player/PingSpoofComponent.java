package cc.unknown.component.impl.player;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import cc.unknown.component.impl.Component;
import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.packet.TimedPacket;
import cc.unknown.util.time.StopWatch;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.util.Tuple;

public final class PingSpoofComponent extends Component {
	public static ConcurrentLinkedQueue<TimedPacket> packets = new ConcurrentLinkedQueue<>();
	static StopWatch enabledTimer = new StopWatch();
	public static boolean enabled;
	static long amount;
	static Tuple<Class[], Boolean> regular = new Tuple<>(new Class[] { C0FPacketConfirmTransaction.class, C00PacketKeepAlive.class, S1CPacketEntityMetadata.class }, false);
	static Tuple<Class[], Boolean> velocity = new Tuple<>(new Class[] { S12PacketEntityVelocity.class, S27PacketExplosion.class }, false);
	static Tuple<Class[], Boolean> teleports = new Tuple<>(new Class[] { S08PacketPlayerPosLook.class, S39PacketPlayerAbilities.class, S09PacketHeldItemChange.class }, false);
	static Tuple<Class[], Boolean> players = new Tuple<>(new Class[] { S13PacketDestroyEntities.class, S14PacketEntity.class, S14PacketEntity.S16PacketEntityLook.class, S14PacketEntity.S15PacketEntityRelMove.class, S14PacketEntity.S17PacketEntityLookMove.class, S18PacketEntityTeleport.class, S20PacketEntityProperties.class, S19PacketEntityHeadLook.class }, false);
	static Tuple<Class[], Boolean> blink = new Tuple<>(new Class[] { C02PacketUseEntity.class, C0DPacketCloseWindow.class, C0EPacketClickWindow.class, C0CPacketInput.class, C0BPacketEntityAction.class, C08PacketPlayerBlockPlacement.class, C07PacketPlayerDigging.class, C09PacketHeldItemChange.class, C13PacketPlayerAbilities.class, C15PacketClientSettings.class, C16PacketClientStatus.class, C17PacketCustomPayload.class, C18PacketSpectate.class, C19PacketResourcePackStatus.class, C03PacketPlayer.class, C03PacketPlayer.C04PacketPlayerPosition.class, C03PacketPlayer.C05PacketPlayerLook.class, C03PacketPlayer.C06PacketPlayerPosLook.class, C0APacketAnimation.class }, false);
	static Tuple<Class[], Boolean> movement = new Tuple<>(new Class[] { C03PacketPlayer.class, C03PacketPlayer.C04PacketPlayerPosition.class, C03PacketPlayer.C05PacketPlayerLook.class, C03PacketPlayer.C06PacketPlayerPosLook.class }, false);

	public static Tuple<Class[], Boolean>[] types = new Tuple[] { regular, velocity, teleports, players, blink, movement };

	@EventLink
	public final Listener<PacketEvent> onPacket = event -> {
		Packet packet = event.getPacket();
		
		if (event.isSend()) {
			event.setCancelled(onPacket(event.getPacket(), event).isCancelled());
		}
		
		if (event.isReceive()) {
			event.setCancelled(onPacket(event.getPacket(), event).isCancelled());
		}
	};

	public CancellableEvent onPacket(Packet<?> packet, CancellableEvent event) {
		if (!event.isCancelled() && enabled && Arrays.stream(types).anyMatch(tuple -> tuple.getSecond()
				&& Arrays.stream(tuple.getFirst()).anyMatch(regularpacket -> regularpacket == packet.getClass()))) {
			event.setCancelled();
			packets.add(new TimedPacket(packet));
		}

		return event;
	}

	public static void dispatch() {
		if (!packets.isEmpty()) {
			// Stops the packets from being called twice
			boolean enabled = PingSpoofComponent.enabled;
			PingSpoofComponent.enabled = false;
			packets.forEach(timedPacket -> PacketUtil.queue(timedPacket.getPacket()));
			PingSpoofComponent.enabled = enabled;
			packets.clear();
		}
	}

	public static void disable() {
		enabled = false;
		enabledTimer.setMillis(enabledTimer.millis - 999999999);
	}

	@EventLink
	public final Listener<WorldChangeEvent> onWorldChange = event -> dispatch();

	@EventLink
	public final Listener<MotionEvent> onMotionEvent = event -> {
		if (event.isPre()) {
			if (!(enabled = !enabledTimer.finished(100) && !(mc.currentScreen instanceof GuiDownloadTerrain))) {
				dispatch();
			} else {
				// Stops the packets from being called twice
				enabled = false;

				packets.forEach(packet -> {
					if (packet.getTime().millis + amount < System.currentTimeMillis()) {
						PacketUtil.queue(packet.getPacket());
						packets.remove(packet);
					}
				});

				enabled = true;
			}
		}
	};

	public static void spoof(int amount, boolean regular, boolean velocity, boolean teleports, boolean players) {
		spoof(amount, regular, velocity, teleports, players, false);
	}

	public static void spoof(int amount, boolean regular, boolean velocity, boolean teleports, boolean players,
			boolean blink, boolean movement) {
		enabledTimer.reset();

		PingSpoofComponent.regular.setSecond(regular);
		PingSpoofComponent.velocity.setSecond(velocity);
		PingSpoofComponent.teleports.setSecond(teleports);
		PingSpoofComponent.players.setSecond(players);
		PingSpoofComponent.blink.setSecond(blink);
		PingSpoofComponent.movement.setSecond(movement);
		PingSpoofComponent.amount = amount;
	}

	public static void spoof(int amount, boolean regular, boolean velocity, boolean teleports, boolean players,
			boolean blink) {
		spoof(amount, regular, velocity, teleports, players, blink, false);
	}

	public static void blink() {
		spoof(9999999, true, false, false, false, true);
	}
}