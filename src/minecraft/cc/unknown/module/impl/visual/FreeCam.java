package cc.unknown.module.impl.visual;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.motion.StrafeEvent;
import cc.unknown.event.impl.other.BlockAABBEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.util.vector.Vector3d;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@ModuleInfo(aliases = { "Free Cam" }, description = "Allows you to leave your player", category = Category.VISUALS)
public final class FreeCam extends Module {

	private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 9.5, 0.1);
	private Vector3d position, delta;
	private Vector2f rotation;
	private boolean sprinting;

	@Override
	public void onEnable() {
		position = new Vector3d(mc.player.posX, mc.player.posY, mc.player.posZ);
		delta = new Vector3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
		rotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
		sprinting = mc.gameSettings.keyBindSprint.isKeyDown();
	}

	@Override
	public void onDisable() {
		mc.player.setPosition(position.getX(), position.getY(), position.getZ());
		mc.player.rotationYaw = rotation.getX();
		mc.player.rotationPitch = rotation.getY();
		mc.player.motionX = delta.getX();
		mc.player.motionY = delta.getY();
		mc.player.motionZ = delta.getZ();
		mc.gameSettings.keyBindSprint.setPressed(sprinting);
	}

	@EventLink
	public final Listener<BlockAABBEvent> blockAABBEventListener = CancellableEvent::setCancelled;

	@EventLink
	public final Listener<PacketEvent> send = event -> {
		Packet<?> packet = event.getPacket();
		if (!event.isSend()) return;
		
		if (packet instanceof C0APacketAnimation || packet instanceof C03PacketPlayer
				|| packet instanceof C02PacketUseEntity || packet instanceof C0BPacketEntityAction
				|| packet instanceof C08PacketPlayerBlockPlacement) {
			event.setCancelled();
		}
	};

	@EventLink
	public final Listener<StrafeEvent> onStrafe = event -> {
		final float speed = this.speed.getValue().floatValue();

		event.setSpeed(speed);
	};

	@EventLink
	public final Listener<MotionEvent> onPreMotionEvent = event -> {
		if (event.isPre()) {
			final float speed = this.speed.getValue().floatValue();

			mc.player.motionY = 0.0D + (mc.gameSettings.keyBindJump.isKeyDown() ? speed : 0.0D)
					- (mc.gameSettings.keyBindSneak.isKeyDown() ? speed : 0.0D);
		}
	};

	@EventLink
	public final Listener<MoveInputEvent> onMovementInput = event -> {
		event.setSneak(false);
	};
}