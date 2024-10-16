package cc.unknown.module.impl.movement.flight;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.module.impl.movement.Flight;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.Mode;
import cc.unknown.value.impl.NumberValue;

/**
 * @author Auth
 * @since 18/11/2021
 */

public class VanillaFlight extends Mode<Flight> {

	private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 9.5, 0.1);

	public VanillaFlight(String name, Flight parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreStrafeEvent> onStrafe = event -> {
		final float speed = this.speed.getValue().floatValue();

		event.setSpeed(speed);
	};

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		final float speed = this.speed.getValue().floatValue();

		mc.player.motionY = 0.0D + (mc.gameSettings.keyBindJump.isKeyDown() ? speed : 0.0D)
				- (mc.gameSettings.keyBindSneak.isKeyDown() ? speed : 0.0D);

	};

	@EventLink
	public final Listener<MoveInputEvent> onMove = event -> {
		event.setSneak(false);
	};

	@Override
	public void onDisable() {
		MoveUtil.stop();
	}
}