package cc.unknown.module.impl.combat;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomUtils;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.FriendAndTargetComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.RayCastUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Aim Assist", description = "Assists you in aiming", category = Category.COMBAT)
public final class AimAssist extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Basic"))
			.add(new SubMode("Advanced"))
			.setDefault("Advanced");

	private final NumberValue horizontalSpeed = new NumberValue("Horizontal speed", this, 5, 1, 20, 0.1, () -> !mode.is("Basic"));
	private final BooleanValue aimVertically = new BooleanValue("Aim vertically", this, false, () -> !mode.is("Basic"));
	private final NumberValue verticalSpeed = new NumberValue("Vertical speed", this, 5, 1, 20, 0.1, () -> !aimVertically.getValue() || !mode.is("Basic"));
	private final NumberValue horizontalAimSpeed = new NumberValue("Horizontal Speed", this, 45.0, 5.0, 100.0, 1.0, () -> !mode.is("Advanced"));
	private final NumberValue horizontalComplement = new NumberValue("Horizontal Complement", this, 15.0, 2.0, 97.0, 1.0, () -> !mode.is("Advanced"));
	private final BooleanValue horizontalRandomization = new BooleanValue("Horizontal Random", this, false, () -> !mode.is("Advanced"));
	private final NumberValue horizontalRandomizationAmount = new NumberValue("Horizontal Randomization", this, 1.2, 0.1, 5, 1, () -> !horizontalRandomization.getValue());
	private final NumberValue maxAngle = new NumberValue("Max Angle", this, 180, 15, 360, 5);
	private final NumberValue distance = new NumberValue("Distance", this, 4, 1, 8, 0.1);
	private final BooleanValue clickAim = new BooleanValue("Aim on Click", this, true);
	private final BooleanValue aimWhileOnTarget = new BooleanValue("Aim while on target", this, true, () -> !mode.is("Basic"));
	private final BooleanValue ignoreFriendlyEntities = new BooleanValue("Ignore Friends", this, false, () -> !mode.is("Advanced"));
	private final BooleanValue ignoreTeammates = new BooleanValue("Ignore Teams", this, false);
	private final BooleanValue scoreboardCheckTeam = new BooleanValue("Scoreboard Check Team", this, false, () -> !ignoreTeammates.getValue());
	private final BooleanValue checkArmorColor = new BooleanValue("Check Armor Color", this, false, () -> !ignoreTeammates.getValue());
	private final BooleanValue ignoreBots = new BooleanValue("Ignore Bots", this, false, () -> !mode.is("Advanced"));
	private final BooleanValue aimAtInvisibleEnemies = new BooleanValue("Aim at Invisible Targets", this, false, () -> !mode.is("Advanced"));
	private final BooleanValue lineOfSightCheck = new BooleanValue("Line of Sight Check", this, true);
	private final BooleanValue mouseOverEntity = new BooleanValue("Mouse Over Entity", this, false, () -> !lineOfSightCheck.getValue());
	private final BooleanValue disableAimWhileBreakingBlock = new BooleanValue("Disable While Breaking Blocks", this, false);
	private final BooleanValue weaponOnly = new BooleanValue("Only Aim While Holding at Weapon", this, false);

	private final Random random = new Random();

	private Double yawNoise = null;
	private Double pitchNoise = null;

	@EventLink
	public final Listener<MotionEvent> onPreMotionEvent = event -> {
		if (event.isPre()) {
			if (noAim()) {
				return;
			}

			final EntityPlayer target = getEnemy();
			if (target == null)
				return;

			if (mode.is("Advanced")) {

			    double advancedSpeed = horizontalAimSpeed.getValue().doubleValue();
			    double advancedCSpeed = horizontalComplement.getValue().doubleValue();
			    float randomness = horizontalRandomizationAmount.getValue().floatValue();

			    Vector2f targetRotations = RotationUtil.calculate(target);
			    double yawFov = PlayerUtil.fovFromEntity(target);

			    if (onTarget(target)) {
				    double horizontalOffset = complementHSpeed(advancedCSpeed);
				    float resultHorizontal = normalHSpeed(yawFov, horizontalOffset, advancedSpeed);

				    if (isYawFov(yawFov)) {
				        float yawChange = changeYaw(randomness);
				        float yawAdjustment = isYawAdjustment(yawChange, resultHorizontal);
				        mc.player.rotationYaw += yawAdjustment;
				    }
			    } else {
				    double horizontalOffset = complementHSpeed(advancedCSpeed);
				    float resultHorizontal = normalHSpeed(yawFov, horizontalOffset, advancedSpeed);
				    
				    if (isYawFov(yawFov)) {
				        float yawChange = changeYaw(randomness);
				        float yawAdjustment = isYawAdjustment(yawChange, resultHorizontal);
				        mc.player.rotationYaw += yawAdjustment;
				    }
			    }
			}

			if (mode.is("Basic")) {
				double deltaYaw = (yawNoise != null) ? yawNoise : 0.0;
				double deltaPitch = (pitchNoise != null) ? pitchNoise : 0.0;
			    
			    double hSpeed = horizontalSpeed.getValue().doubleValue();
			    double vSpeed = verticalSpeed.getValue().doubleValue();

			    if (onTarget(target)) {
			        if (aimWhileOnTarget.getValue()) {
			            hSpeed *= 0.85;
			            vSpeed *= 0.85;
			        } else {
			            hSpeed = 0;
			            vSpeed = 0;
			        }
			    }

			    Vector2f targetRotation = RotationUtil.calculate(target);
			    float targetYaw = targetRotation.x;
			    float targetPitch = targetRotation.y;

			    boolean move = false;

			    final float curYaw = mc.player.rotationYaw;
			    final float curPitch = mc.player.rotationPitch;

			    if (targetYaw > curYaw) {
			        move = true;
			        Vector2f movedRotation = RotationUtil.move(new Vector2f(curYaw, curPitch), new Vector2f(targetYaw, curPitch), hSpeed);
			        deltaYaw += movedRotation.x;
			    } else if (targetYaw < curYaw) {
			        move = true;
			        Vector2f movedRotation = RotationUtil.move(new Vector2f(curYaw, curPitch), new Vector2f(targetYaw, curPitch), hSpeed);
			        deltaYaw += movedRotation.x;
			    }

			    if (aimVertically.getValue()) {
			        if (targetPitch > curPitch) {
			            move = true;
			            Vector2f movedRotation = RotationUtil.move(new Vector2f(curYaw, curPitch), new Vector2f(curYaw, targetPitch), vSpeed);
			            deltaPitch += movedRotation.y;
			        } else if (targetPitch < curPitch) {
			            move = true;
			            Vector2f movedRotation = RotationUtil.move(new Vector2f(curYaw, curPitch), new Vector2f(curYaw, targetPitch), vSpeed);
			            deltaPitch += movedRotation.y;
			        }
			    }

			    if (move) {
			        deltaYaw += (Math.random() - 0.5) * Math.min(0.8, deltaPitch / 10.0);
			        deltaPitch += (Math.random() - 0.5) * Math.min(0.8, deltaYaw / 10.0);
			    }

			    mc.player.rotationYaw += (float) deltaYaw;
			    mc.player.rotationPitch += (float) deltaPitch;
			}
		}
	};

	@Override
	public void onDisable() {
		yawNoise = pitchNoise = null;
	}

	public EntityPlayer getEnemy() {
		final int fov = maxAngle.getValue().intValue();
		final List<EntityPlayer> players = mc.world.playerEntities;
		final Vec3 playerPos = new Vec3(mc.player);

		EntityPlayer target = null;
		double targetFov = Double.MAX_VALUE;
		for (final EntityPlayer entityPlayer : players) {
			if (entityPlayer != mc.player && entityPlayer.deathTime == 0) {
				double dist = playerPos.distanceTo(entityPlayer);
				if (FriendAndTargetComponent.isTarget(entityPlayer))
					continue;
				if (FriendAndTargetComponent.isFriend(entityPlayer) && ignoreFriendlyEntities.getValue())
					continue;
				if (Sakura.instance.getBotManager().contains(entityPlayer) && ignoreBots.getValue())
					continue;
				if (ignoreTeammates.getValue() && PlayerUtil.isTeam(entityPlayer, scoreboardCheckTeam.getValue(), checkArmorColor.getValue()))
					continue;
				if (dist > distance.getValue().doubleValue())
					continue;
				if (fov != 360 && !PlayerUtil.inFov(fov, entityPlayer))
					continue;
				if (lineOfSightCheck.getValue() && !mc.player.canEntityBeSeen(entityPlayer))
					continue;
				double curFov = Math.abs(PlayerUtil.getFov(entityPlayer.posX, entityPlayer.posZ));
				if (curFov < targetFov) {
					target = entityPlayer;
					targetFov = curFov;
				}
			}
		}
		return target;
	}

	private boolean noAim() {
		if (mc.currentScreen != null || !mc.inGameHasFocus)
			return true;
		if (weaponOnly.getValue() && !PlayerUtil.isHoldingWeapon())
			return true;
		if (clickAim.getValue() && !PlayerUtil.isClicking())
			return true;
		if (mouseOverEntity.getValue() && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY)
			return true;
		return disableAimWhileBreakingBlock.getValue() && mc.playerController.isHittingBlock;
	}

	private boolean onTarget(EntityPlayer target) {
		return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY
				&& mc.objectMouseOver.entityHit == target;
	}

	private double complementHSpeed(double complement) {
		return ThreadLocalRandom.current().nextDouble(complement - 1.47328, complement + 2.48293) / 100;
	}

	private float normalHSpeed(double fov, double offset, double speed) {
		return (float) (-(fov * offset
				+ fov / (101.0D - ThreadLocalRandom.current().nextDouble(speed - 4.723847, speed))));
	}

	private boolean isYawFov(double fov) {
		return fov > 1.0D || fov < -1.0D;
	}

	private float changeYaw(float randomize) {
		return random.nextBoolean() ? -RandomUtils.nextFloat(0F, randomize) : RandomUtils.nextFloat(0F, randomize);
	}

	private float isYawAdjustment(float change, float result) {
		return horizontalRandomization.getValue() ? change : result;
	}
}
