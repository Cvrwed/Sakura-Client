package cc.unknown.util.rotation;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.util.Accessor;
import cc.unknown.util.RayCastUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.util.vector.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * @author Patrick
 * @since 11/17/2021
 */

public class RotationUtil implements Accessor {

	public static Vector2f calculate(final Vector3d from, final Vector3d to) {
		final Vector3d diff = to.subtract(from);
		final double distance = Math.hypot(diff.getX(), diff.getZ());
		final float yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * (float) (180.0F / Math.PI)) - 90.0F;
		final float pitch = (float) (-(MathHelper.atan2(diff.getY(), distance) * (float) (180.0F / Math.PI)));
		return new Vector2f(yaw, pitch);
	}

	public static Vector2f calculate(final Entity entity) {
		return calculate(entity.getCustomPositionVector().add(0,
				Math.max(0, Math.min(mc.player.posY - entity.posY + mc.player.getEyeHeight(),
						(entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * 0.9)),
				0));
	}

	public static float[] getFixedRotation(final float[] rotations, final float[] lastRotations) {
		final Minecraft mc = Minecraft.getMinecraft();

		final float yaw = rotations[0];
		final float pitch = rotations[1];

		final float lastYaw = lastRotations[0];
		final float lastPitch = lastRotations[1];

		final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float gcd = f * f * f * 1.2F;

		final float deltaYaw = yaw - lastYaw;
		final float deltaPitch = pitch - lastPitch;

		final float fixedDeltaYaw = deltaYaw - (deltaYaw % gcd);
		final float fixedDeltaPitch = deltaPitch - (deltaPitch % gcd);

		final float fixedYaw = lastYaw + fixedDeltaYaw;
		final float fixedPitch = lastPitch + fixedDeltaPitch;

		return new float[] { fixedYaw, fixedPitch };
	}

	public static Vector2f calculate(final Entity entity, final boolean adaptive, final double range) {
		Vector2f normalRotations = calculate(entity);
		if (!adaptive || RayCastUtil.rayCast(normalRotations, range).typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
			return normalRotations;
		}

		for (double yPercent = 1; yPercent >= 0; yPercent -= 0.25 + Math.random() * 0.1) {
			for (double xPercent = 1; xPercent >= -0.5; xPercent -= 0.5) {
				for (double zPercent = 1; zPercent >= -0.5; zPercent -= 0.5) {
					Vector2f adaptiveRotations = calculate(entity.getCustomPositionVector().add(
							(entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX) * xPercent,
							(entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * yPercent,
							(entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ) * zPercent));

					if (RayCastUtil.rayCast(adaptiveRotations, range).typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
						return adaptiveRotations;
					}
				}
			}
		}
		return normalRotations;
	}

	public Vector2f calculate(final Vec3 to, final EnumFacing enumFacing) {
		return calculate(new Vector3d(to.xCoord, to.yCoord, to.zCoord), enumFacing);
	}

	public static Vector2f calculate(final Vec3 to) {
		return calculate(mc.player.getCustomPositionVector().add(0, mc.player.getEyeHeight(), 0),
				new Vector3d(to.xCoord, to.yCoord, to.zCoord));
	}

	public static Vector2f calculate(final BlockPos to) {
		return calculate(mc.player.getCustomPositionVector().add(0, mc.player.getEyeHeight(), 0),
				new Vector3d(to.getX(), to.getY(), to.getZ()).add(0.5, 0.5, 0.5));
	}

	public static Vector2f calculate(final Vector3d to) {
		return calculate(mc.player.getCustomPositionVector().add(0, mc.player.getEyeHeight(), 0), to);
	}

	public static Vector2f calculate(final Vector3d position, final EnumFacing enumFacing) {
		double x = position.getX() + 0.5D;
		double y = position.getY() + 0.5D;
		double z = position.getZ() + 0.5D;

		x += (double) enumFacing.getDirectionVec().getX() * 0.5D;
		y += (double) enumFacing.getDirectionVec().getY() * 0.5D;
		z += (double) enumFacing.getDirectionVec().getZ() * 0.5D;
		return calculate(new Vector3d(x, y, z));
	}

	public static Vector2f applySensitivityPatch(final Vector2f rotation) {
		final Vector2f previousRotation = mc.player.getPreviousRotation();
		final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F
				+ 0.2F);
		final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
		final float yaw = previousRotation.x
				+ (float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier);
		final float pitch = previousRotation.y
				+ (float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier);
		return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90, 90));
	}

	public static Vector2f applySensitivityPatch(final Vector2f rotation, final Vector2f previousRotation) {
		final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F
				+ 0.2F);
		final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
		final float yaw = previousRotation.x
				+ (float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier);
		final float pitch = previousRotation.y
				+ (float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier);
		return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90, 90));
	}

	public Vector2f relateToPlayerRotation(final Vector2f rotation) {
		final Vector2f previousRotation = mc.player.getPreviousRotation();
		final float yaw = previousRotation.x + MathHelper.wrapAngleTo180_float(rotation.x - previousRotation.x);
		final float pitch = MathHelper.clamp_float(rotation.y, -90, 90);
		return new Vector2f(yaw, pitch);
	}

	public static Vector2f resetRotation(final Vector2f rotation) {
		if (rotation == null) {
			return null;
		}

		final float yaw = rotation.x + MathHelper.wrapAngleTo180_float(mc.player.rotationYaw - rotation.x);
		final float pitch = mc.player.rotationPitch;
		return new Vector2f(yaw, pitch);
	}

	public static Vector2f move(final Vector2f targetRotation, final double speed) {
		return move(RotationComponent.lastRotations, targetRotation, speed);
	}

	public static Vector2f move(final Vector2f lastRotation, final Vector2f targetRotation, double speed) {
		if (speed != 0) {

			double deltaYaw = MathHelper.wrapAngleTo180_float(targetRotation.x - lastRotation.x);
			final double deltaPitch = (targetRotation.y - lastRotation.y);

			final double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
			final double distributionYaw = Math.abs(deltaYaw / distance);
			final double distributionPitch = Math.abs(deltaPitch / distance);

			final double maxYaw = speed * distributionYaw;
			final double maxPitch = speed * distributionPitch;

			final float moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
			final float movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

			return new Vector2f(moveYaw, movePitch);
		}

		return new Vector2f(0, 0);
	}

	public static Vector2f smooth(final Vector2f targetRotation, final double speed) {
		return smooth(RotationComponent.lastRotations, targetRotation, speed);
	}

	public static Vector2f smooth(final Vector2f lastRotation, final Vector2f targetRotation, final double speed) {
		float yaw = targetRotation.x;
		float pitch = targetRotation.y;
		final float lastYaw = lastRotation.x;
		final float lastPitch = lastRotation.y;

		if (speed != 0) {
			Vector2f move = move(targetRotation, speed);

			yaw = lastYaw + move.x;
			pitch = lastPitch + move.y;

			for (int i = 1; i <= (int) (Minecraft.getDebugFPS() / 20f + Math.random() * 10); ++i) {

				if (Math.abs(move.x) + Math.abs(move.y) > 0.0001) {
					yaw += (Math.random() - 0.5) / 1000;
					pitch -= Math.random() / 200;
				}

				/*
				 * Fixing GCD
				 */
				final Vector2f rotations = new Vector2f(yaw, pitch);
				final Vector2f fixedRotations = applySensitivityPatch(rotations);

				/*
				 * Setting rotations
				 */
				yaw = fixedRotations.x;
				pitch = Math.max(-90, Math.min(90, fixedRotations.y));
			}
		}

		return new Vector2f(yaw, pitch);
	}
}