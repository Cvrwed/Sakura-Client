package cc.unknown.component.impl.player;

import java.util.function.Function;

import cc.unknown.component.impl.Component;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.event.impl.render.LookEvent;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.util.vector.Vector2f;
import net.minecraft.util.MathHelper;

public final class RotationComponent extends Component {
	private static boolean active, smoothed;
	public static Vector2f rotations, lastRotations = new Vector2f(0, 0), targetRotations, lastServerRotations;
	private static double rotationSpeed;
	private static MovementFix correctMovement;
	private static Function<Vector2f, Boolean> raycast;
	private static float randomAngle;
	private static final Vector2f offset = new Vector2f(0, 0);

	/*
	 * This method must be called on Pre Update Event to work correctly
	 */
	public static void setRotations(final Vector2f rotations, final double rotationSpeed,
			final MovementFix correctMovement) {
		setRotations(rotations, rotationSpeed, correctMovement, null);
	}

	/*
	 * This method must be called on Pre Update Event to work correctly
	 */
	public static void setRotations(final Vector2f rotations, final double rotationSpeed,
			final MovementFix correctMovement, final Function<Vector2f, Boolean> raycast) {
		RotationComponent.targetRotations = rotations;
		RotationComponent.rotationSpeed = rotationSpeed * 36;
		RotationComponent.correctMovement = correctMovement;
		RotationComponent.raycast = raycast;
		active = true;

		smooth();
	}

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
		if (!active || rotations == null || lastRotations == null || targetRotations == null
				|| lastServerRotations == null) {
			rotations = lastRotations = targetRotations = lastServerRotations = new Vector2f(mc.player.rotationYaw,
					mc.player.rotationPitch);
		}

		if (active) {
			smooth();
		}

		if (correctMovement == MovementFix.BACKWARDS_SPRINT && active) {
			if (Math.abs(rotations.x % 360 - Math.toDegrees(MoveUtil.direction()) % 360) > 45) {
				mc.gameSettings.keyBindSprint.setPressed(false);
				mc.player.setSprinting(false);
			}
		}
	};

	@EventLink(value = Priority.LOW)
	public final Listener<MoveInputEvent> onMove = event -> {
		if (active && correctMovement == MovementFix.SILENT && rotations != null) {
			/*
			 * Calculating movement fix
			 */
			final float yaw = rotations.x;
			MoveUtil.fixMovement(event, yaw);
		}
	};

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<LookEvent> onLook = event -> {
		if (active && rotations != null) {
			event.setRotation(rotations);
		}
	};

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<PreStrafeEvent> onStrafe = event -> {
		if (active && (correctMovement == MovementFix.SILENT || correctMovement == MovementFix.STRICT)
				&& rotations != null) {
			event.setYaw(rotations.x);
		}
	};

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<JumpEvent> onJump = event -> {
		if (active && (correctMovement == MovementFix.SILENT || correctMovement == MovementFix.STRICT
				|| correctMovement == MovementFix.BACKWARDS_SPRINT) && rotations != null) {
			event.setYaw(rotations.x);
		}
	};

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		if (active && rotations != null) {
			final float yaw = rotations.x;
			final float pitch = rotations.y;

			event.setYaw(yaw);
			event.setPitch(pitch);

			mc.player.rotationYawHead = yaw;
			mc.player.renderPitchHead = pitch;

			lastServerRotations = new Vector2f(yaw, pitch);

			if (Math.abs((rotations.x - mc.player.rotationYaw) % 360) < 1
					&& Math.abs((rotations.y - mc.player.rotationPitch)) < 1) {
				active = false;

				this.correctDisabledRotations();
			}

			lastRotations = rotations;
		} else {
			lastRotations = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
		}

		targetRotations = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
		smoothed = false;

	};

	private void correctDisabledRotations() {
		final Vector2f rotations = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
		final Vector2f fixedRotations = RotationUtil
				.resetRotation(RotationUtil.applySensitivityPatch(rotations, lastRotations));

		mc.player.rotationYaw = fixedRotations.x;
		mc.player.rotationPitch = fixedRotations.y;
	}

	public static void smooth() {
		if (!smoothed) {
			float targetYaw = targetRotations.x;
			float targetPitch = targetRotations.y;

			// Randomisation
			if (raycast != null && (Math.abs(targetYaw - rotations.x) > 5 || Math.abs(targetPitch - rotations.y) > 5)) {
				final Vector2f trueTargetRotations = new Vector2f(targetRotations.getX(), targetRotations.getY());

				double speed = (Math.random() * Math.random() * Math.random()) * 20;
				randomAngle += (float) ((20
						+ (float) (Math.random() - 0.5) * (Math.random() * Math.random() * Math.random() * 360))
						* (mc.player.ticksExisted / 10 % 2 == 0 ? -1 : 1));

				offset.setX((float) (offset.getX() + -MathHelper.sin((float) Math.toRadians(randomAngle)) * speed));
				offset.setY((float) (offset.getY() + MathHelper.cos((float) Math.toRadians(randomAngle)) * speed));

				targetYaw += offset.getX();
				targetPitch += offset.getY();

				if (!raycast.apply(new Vector2f(targetYaw, targetPitch))) {
					randomAngle = (float) Math.toDegrees(Math.atan2(trueTargetRotations.getX() - targetYaw,
							targetPitch - trueTargetRotations.getY())) - 180;

					targetYaw -= offset.getX();
					targetPitch -= offset.getY();

					offset.setX((float) (offset.getX() + -MathHelper.sin((float) Math.toRadians(randomAngle)) * speed));
					offset.setY((float) (offset.getY() + MathHelper.cos((float) Math.toRadians(randomAngle)) * speed));

					targetYaw = targetYaw + offset.getX();
					targetPitch = targetPitch + offset.getY();
				}

				if (!raycast.apply(new Vector2f(targetYaw, targetPitch))) {
					offset.setX(0);
					offset.setY(0);

					targetYaw = (float) (targetRotations.x + Math.random() * 2);
					targetPitch = (float) (targetRotations.y + Math.random() * 2);
				}
			}

			rotations = RotationUtil.smooth(new Vector2f(targetYaw, targetPitch), rotationSpeed + Math.random());

			if (correctMovement == MovementFix.SILENT || correctMovement == MovementFix.STRICT) {
				mc.player.movementYaw = rotations.x;
			}

			mc.player.velocityYaw = rotations.x;
		}

		smoothed = true;

		/*
		 * Updating MouseOver
		 */
		mc.entityRenderer.getMouseOver(1);
	}

	public static boolean isActive() {
		return active;
	}

	public static boolean isSmoothed() {
		return smoothed;
	}

	public static Vector2f getRotations() {
		return rotations;
	}

	public static Vector2f getLastRotations() {
		return lastRotations;
	}

	public static Vector2f getTargetRotations() {
		return targetRotations;
	}

	public static Vector2f getLastServerRotations() {
		return lastServerRotations;
	}

	public static double getRotationSpeed() {
		return rotationSpeed;
	}

	public static MovementFix getCorrectMovement() {
		return correctMovement;
	}

	public static Function<Vector2f, Boolean> getRaycast() {
		return raycast;
	}

	public static float getRandomAngle() {
		return randomAngle;
	}

	public static Vector2f getOffset() {
		return offset;
	}

	public static void setActive(boolean active) {
		RotationComponent.active = active;
	}

	public static void setSmoothed(boolean smoothed) {
		RotationComponent.smoothed = smoothed;
	}

	public static void setRotations(Vector2f rotations) {
		RotationComponent.rotations = rotations;
	}

	public static void setLastRotations(Vector2f lastRotations) {
		RotationComponent.lastRotations = lastRotations;
	}

	public static void setTargetRotations(Vector2f targetRotations) {
		RotationComponent.targetRotations = targetRotations;
	}

	public static void setLastServerRotations(Vector2f lastServerRotations) {
		RotationComponent.lastServerRotations = lastServerRotations;
	}

	public static void setRotationSpeed(double rotationSpeed) {
		RotationComponent.rotationSpeed = rotationSpeed;
	}

	public static void setCorrectMovement(MovementFix correctMovement) {
		RotationComponent.correctMovement = correctMovement;
	}

	public static void setRaycast(Function<Vector2f, Boolean> raycast) {
		RotationComponent.raycast = raycast;
	}

	public static void setRandomAngle(float randomAngle) {
		RotationComponent.randomAngle = randomAngle;
	}
}