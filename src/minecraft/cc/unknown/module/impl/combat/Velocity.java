package cc.unknown.module.impl.combat;

import java.util.concurrent.TimeUnit;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleInfo(aliases = "Velocity", description = "Uses heavy dick and balls to drag across the floor to reduce velocity.", category = Category.COMBAT)
public final class Velocity extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Simple"))
			.add(new SubMode("Legit"))
			.add(new SubMode("Polar"))
			.add(new SubMode("Watchdog Simple"))
			.setDefault("Standard");

	private final NumberValue horizontal = new NumberValue("Horizontal", this, 0, 0, 100, 1, () -> !mode.is("Simple"));
	private final NumberValue vertical = new NumberValue("Vertical", this, 0, 0, 100, 1, () -> !mode.is("Simple"));
	
	private final NumberValue chance = new NumberValue("Chance", this, 100, 0, 100, 1, () -> mode.is("Jump"));
	
	private final BooleanValue onSwing = new BooleanValue("On Swing", this, false);
	public final BooleanValue legitTiming = new BooleanValue("Legit Timing", this, true, () -> !mode.is("Legit"));

	private boolean reduced;

	@Override
	public void onEnable() {
		reduced = false;
	}

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<PacketEvent> onPacket = event -> {
		if ((onSwing.getValue() && !mc.player.isSwingInProgress) || event.isCancelled()) {
			return;
		}
		
		if (chance.getValue().intValue() != 100 && Math.random() >= chance.getValue().doubleValue() / 100.0) {
			return;
		}
		
		final Packet<?> p = event.getPacket();

		final double horizontal = this.horizontal.getValue().doubleValue();
		final double vertical = this.vertical.getValue().doubleValue();

		String name = mode.getValue().getName();

		if (event.isReceive()) {
			if (p instanceof S12PacketEntityVelocity) {
				final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) p;
	
				if (wrapper.getEntityID() == mc.player.getEntityId()) {
					switch (name) {
					case "Simple":
						if (wrapper.getMotionY() / 8000.0D > 0.6) {
							return;
						}
	
						if (horizontal == 0) {
							if (vertical != 0 && !event.isCancelled()) {
								mc.player.motionY = wrapper.getMotionY() / 8000.0D;
							}
	
							event.setCancelled();
							return;
						}
	
						wrapper.motionX *= horizontal / 100;
						wrapper.motionY *= vertical / 100;
						wrapper.motionZ *= horizontal / 100;
	
						event.setPacket(wrapper);
						break;
	
					case "Legit":
						if (mc.player.onGround && wrapper.motionY > 0) {
							if (!legitTiming.getValue() || mc.player.ticksSinceVelocity <= 14 || mc.player.onGroundTicks <= 1) {
								reduced = true;
							}
						}
						break;
					case "Polar":
						if (mc.player.hurtTime >= 5 && mc.player.onGround) {
							mc.player.jump();
						}
						break;
	
					case "Watchdog Simple":
						event.setCancelled();
	
						if (mc.player.posY < mc.player.lastGroundY + 0.5) {
							mc.player.motionY = wrapper.getMotionY() / 8000.0D;
						}
						break;
					}
				}
			}
		}
	};

	@EventLink
	public final Listener<MotionEvent> onPreMotionEvent = event -> {
		if (event.isPre()) {
			String name = mode.getValue().getName();

			if (name.equals("Legit")) {
				reduced = false;
			}
		}
	};

	@EventLink
	public final Listener<MoveInputEvent> onMove = event -> {
		String name = mode.getValue().getName();

		if (name.equals("Legit")) {
			if (reduced && MoveUtil.isMoving()) {
				event.setJump(true);
			}
		}
	};
}
