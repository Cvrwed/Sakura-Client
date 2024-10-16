package cc.unknown.module.impl.world;

import java.util.Objects;
import java.util.function.BiConsumer;

import org.lwjgl.input.Keyboard;

import cc.unknown.component.impl.player.BadPacketsComponent;
import cc.unknown.component.impl.player.FallDistanceComponent;
import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.KeyboardInputEvent;
import cc.unknown.event.impl.input.MoveInputEvent;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.player.PreStrafeEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.movement.Speed;
import cc.unknown.module.impl.world.scaffold.down.NormalDownward;
import cc.unknown.module.impl.world.scaffold.sprint.BypassSprint;
import cc.unknown.module.impl.world.scaffold.sprint.DisabledSprint;
import cc.unknown.module.impl.world.scaffold.sprint.LegitSprint;
import cc.unknown.module.impl.world.scaffold.sprint.MatrixSprint;
import cc.unknown.module.impl.world.scaffold.tower.MMCTower;
import cc.unknown.module.impl.world.scaffold.tower.NCPTower;
import cc.unknown.module.impl.world.scaffold.tower.NormalTower;
import cc.unknown.module.impl.world.scaffold.tower.PolarTower;
import cc.unknown.module.impl.world.scaffold.tower.VanillaTower;
import cc.unknown.util.RayCastUtil;
import cc.unknown.util.math.MathUtil;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.EnumFacingOffset;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.SlotUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.util.vector.Vector3d;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.StringValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

@ModuleInfo(aliases = { "Scaffold",
		"scaff", "auto bridge" }, description = "Builds a bridge under you as you walk", category = Category.WORLD)
public class Scaffold extends Module {

	public final ModeValue mode = new ModeValue("Rotation Mode", this)
			.add(new SubMode("Normal"))
			.add(new SubMode("Godbridge"))
			.add(new SubMode("Breesily"))
			.add(new SubMode("Snap"))
			.add(new SubMode("Telly"))
			.add(new SubMode("Legit"))
			.setDefault("Normal");

	public final ModeValue rayCast = new ModeValue("Ray Cast", this)
			.add(new SubMode("Off"))
			.add(new SubMode("Normal"))
			.add(new SubMode("Strict"))
			.setDefault("Strict");

	public final ModeValue sprint = new ModeValue("Sprint", this)
			.add(new SubMode("Normal"))
			.add(new DisabledSprint("Disabled", this))
			.add(new LegitSprint("Legit", this))
			.add(new BypassSprint("Bypass", this))
			.add(new MatrixSprint("Matrix", this))
			.setDefault("Normal");

	public final ModeValue tower = new ModeValue("Tower", this)
			.add(new SubMode("Disabled"))
			.add(new VanillaTower("Vanilla", this))
			.add(new NormalTower("Normal", this))
			.add(new MMCTower("MMC", this))
			.add(new NCPTower("NCP", this))
			.add(new PolarTower("Polar", this))
			.setDefault("Disabled");

	public final ModeValue sameYValue = new ModeValue("Same Y", this)
			.add(new SubMode("Off"))
			.add(new SubMode("On"))
			.add(new SubMode("Auto Jump"))
			.setDefault("Off");
	
    private final StringValue macroKey = new StringValue("Macro Key:", this, "Y");

	public final ModeValue downwards = new ModeValue("Downwards (Press Sneak)", this)
			.add(new SubMode("Off"))
			.add(new NormalDownward("Normal", this))
			.setDefault("Off");

	private final BoundsNumberValue rotationSpeed = new BoundsNumberValue("Rotation Speed", this, 5, 10, 0, 10, 1);
	public final BoundsNumberValue placeDelay = new BoundsNumberValue("Place Delay", this, 0, 0, 0, 5, 1);
	private final NumberValue timer = new NumberValue("Timer", this, 1, 0.1, 10, 0.1);
	private final NumberValue expand = new NumberValue("Expand", this, 0, 0, 5, 1);
	public final BooleanValue movementCorrection = new BooleanValue("Movement Correction", this, false);
	public final BooleanValue safeWalk = new BooleanValue("Safe Walk", this, false);
	
	private final BooleanValue sneak = new BooleanValue("Sneak", this, false);
	public final BoundsNumberValue startSneaking = new BoundsNumberValue("Start Sneaking", this, 0, 0, 0, 5, 1, () -> !sneak.getValue());
	public final BoundsNumberValue stopSneaking = new BoundsNumberValue("Stop Sneaking", this, 0, 0, 0, 5, 1, () -> !sneak.getValue());
	public final BoundsNumberValue sneakEvery = new BoundsNumberValue("Sneak every x blocks", this, 1, 1, 1, 10, 1, () -> !sneak.getValue());
	public final NumberValue sneakingSpeed = new NumberValue("Sneaking Speed", this, 0.2, 0.2, 1, 0.05, () -> !sneak.getValue());

	private final BooleanValue advanced = new BooleanValue("Advanced", this, false);

	public final ModeValue yawOffset = new ModeValue("Yaw Offset", this, () -> !advanced.getValue())
			.add(new SubMode("0"))
			.add(new SubMode("45"))
			.add(new SubMode("-45"))
			.setDefault("0");
	
	private final NumberValue yawOffsetRandomization = new NumberValue("Yaw Offset Randomization", this, 5, 5, 360, 5, () -> !advanced.getValue());

	public final BooleanValue ignoreSpeed = new BooleanValue("Ignore Speed Effect", this, false, () -> !advanced.getValue());
	private Vec3 targetBlock;
	private EnumFacingOffset enumFacing;
	public Vec3i offset = new Vec3i(0, 0, 0);
	private BlockPos blockFace;
	private float targetYaw;
	private float targetPitch;
	private float forward;
	private float strafe;
	private float yawDrift;
	private float pitchDrift;
	private int ticksOnAir;
	private int sneakingTicks;
	private int placements;
	private int slow;
	private int pause;
	public int recursions, recursion;
	public double startY;
	private boolean canPlace;
	private boolean sameY;
	private int directionalChange;
	private int blockCount;

	@Override
	public void onEnable() {
		targetYaw = mc.player.rotationYaw - 180 + Integer.parseInt(yawOffset.getValue().getName());
		targetPitch = 90;

		pitchDrift = (float) ((Math.random() - 0.5) * (Math.random() - 0.5) * 10);
		yawDrift = (float) ((Math.random() - 0.5) * (Math.random() - 0.5) * 10);

		startY = Math.floor(mc.player.posY);
		targetBlock = null;

		this.sneakingTicks = 0;
		recursions = 0;
		placements = 0;
	}

	@Override
	public void onDisable() {
		resetBinds();
	}

	public void resetBinds() {
		resetBinds(true, true, true, true, true, true);
	}

	public void resetBinds(boolean sneak, boolean jump, boolean right, boolean left, boolean forward, boolean back) {
		BiConsumer<Boolean, Runnable> setKeyBind = (condition, action) -> {
			if (condition)
				action.run();
		};

		setKeyBind.accept(sneak, () -> mc.gameSettings.keyBindSneak
				.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())));
		setKeyBind.accept(jump, () -> mc.gameSettings.keyBindJump
				.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())));
		setKeyBind.accept(right, () -> mc.gameSettings.keyBindRight
				.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())));
		setKeyBind.accept(left, () -> mc.gameSettings.keyBindLeft
				.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())));
		setKeyBind.accept(forward, () -> mc.gameSettings.keyBindForward
				.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())));
		setKeyBind.accept(back, () -> mc.gameSettings.keyBindBack
				.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())));
	}
	@EventLink(value = Priority.VERY_HIGH)
	public final Listener<PacketEvent> onPacket = event -> {
		if (event.isReceive()) {
			PacketUtil.correctBlockCount(event);
		}
	};

	@EventLink
	public final Listener<KeyboardInputEvent> onKeyboard = event -> {
	    try {
	        String keyFieldName = "KEY_" + macroKey.getValue().toUpperCase();
	        
	        int macroKeyCode = Keyboard.class.getField(keyFieldName).getInt(null);

	        if (event.getKeyCode() == macroKeyCode) {
	            if (sameYValue.is("Off")) {
	            	startY = Math.floor(mc.player.posY);
	                sameYValue.setDefault("Auto Jump");
	            } else {
	                sameYValue.setDefault("Off");
	            }
	        }
	    } catch (NoSuchFieldException | IllegalAccessException e) {
	        e.printStackTrace();
	    }
	};
	
	public void calculateSneaking(MoveInputEvent moveInputEvent) {
		forward = moveInputEvent.getForward();
		strafe = moveInputEvent.getStrafe();

		if (slow > 0) {
			moveInputEvent.setForward(0);
			moveInputEvent.setStrafe(0);
			slow--;
			return;
		}

		if (!this.sneak.getValue()) {
			return;
		}

		double speed = this.sneakingSpeed.getValue().doubleValue();

		if (speed <= 0.2) {
			return;
		}

		moveInputEvent.setSneakSlowDownMultiplier(speed);
	}

	public void calculateSneaking() {
		if (ticksOnAir == 0)
			mc.gameSettings.keyBindSneak.setPressed(false);

		this.sneakingTicks--;

		if (!this.sneak.getValue() && pause <= 0) {
			return;
		}

		int ahead = startSneaking.getRandomBetween().intValue();
		int place = placeDelay.getRandomBetween().intValue();
		int after = stopSneaking.getRandomBetween().intValue();

		if (pause > 0) {
			pause--;

			sneakingTicks = 0;
			placements = 0;
		}

		if (this.sneakingTicks >= 0) {
			mc.gameSettings.keyBindSneak.setPressed(true);
			return;
		}

		if (ticksOnAir > 0) {
			this.sneakingTicks = (int) (double) (after);
		}

		if (ticksOnAir > 0 || PlayerUtil.blockRelativeToPlayer(mc.player.motionX * ahead, MoveUtil.HEAD_HITTER_MOTION,
				mc.player.motionZ * ahead) instanceof BlockAir) {
			if (placements <= 0) {
				this.sneakingTicks = (int) (double) (ahead + place + after);
				placements = sneakEvery.getRandomBetween().intValue();
			}
		}
	}

	public void calculateRotations() {
		int yawOffset = Integer.parseInt(String.valueOf(this.yawOffset.getValue().getName()));

		/* Smoothing rotations */
		final double minRotationSpeed = this.rotationSpeed.getValue().doubleValue();
		final double maxRotationSpeed = this.rotationSpeed.getSecondValue().doubleValue();
		float rotationSpeed = (float) MathUtil.getRandom(minRotationSpeed, maxRotationSpeed);

		MovementFix movementFix = this.movementCorrection.getValue() ? MovementFix.SILENT : MovementFix.OFF;

		/* Calculating target rotations */
		switch (mode.getValue().getName()) {
		case "Normal":
			mc.entityRenderer.getMouseOver(1);

			if (canPlace && !mc.gameSettings.keyBindPickBlock.isKeyDown()) {
				if (mc.objectMouseOver.sideHit != enumFacing.getEnumFacing()
						|| !mc.objectMouseOver.getBlockPos().equals(blockFace)) {
					getRotations(yawOffset);
				}
			}
			break;
			
		case "Snap":
		    boolean shouldGetMouseOver = false;
		    getRotations(yawOffset);
		    
		    if (mc.player.onGround) {
		        boolean isAirTick = ticksOnAir > 0 && !RayCastUtil.overBlock(
		            RotationComponent.rotations, enumFacing.getEnumFacing(), blockFace, rayCast.is("Normal"));

		        if (!isAirTick)
		            targetYaw = (float) Math.toDegrees(MoveUtil.direction(mc.player.rotationYaw, forward, strafe)) - yawOffset;
		        

		        if (PlayerUtil.isOverAir()) {
		            shouldGetMouseOver = true;
		        }

		        if (mc.player.hurtTime > 0 && FallDistanceComponent.distance > 0 && !PlayerUtil.isBlockUnder() && mc.player.posY + mc.player.motionY < Math.floor(mc.player.posY)) {
		            shouldGetMouseOver = true;
		        }
		        
		        if (sameYValue.is("Off") && PlayerUtil.isOnEdge()) {
		            shouldGetMouseOver = true;
		        }
		    } else {
		        shouldGetMouseOver = mc.player.motionY > 0 || mc.player.hurtTime > 0;
		    }

		    if (shouldGetMouseOver) {
		        mc.entityRenderer.getMouseOver(1);
		        getRotations(yawOffset);
		    }
		    break;

		case "Breesily":
			if (canPlace) {
				if (enumFacing.getEnumFacing() == EnumFacing.UP) {
					targetPitch = 90;
				} else {
					double staticYaw = (float) (Math.toDegrees(Math.atan2(enumFacing.getOffset().zCoord, enumFacing.getOffset().xCoord)) % 360) - 90;
					double staticPitch = 80;

					targetYaw = (float) staticYaw + yawDrift;
					targetPitch = (float) staticPitch + pitchDrift;
				}
			} else if (Math.random() > 0.99 || targetPitch % 90 == 0) {
				yawDrift = (float) (Math.random() - 0.5);
				pitchDrift = (float) (Math.random() - 0.5);
			}

			if (mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) {
				double offset = 0;
				double speed = 0;

				switch (mc.player.getHorizontalFacing()) {
				case NORTH:
					offset = mc.player.posX - Math.floor(mc.player.posX);
					speed = mc.player.motionZ;
					break;

				case EAST:
					offset = mc.player.posZ - Math.floor(mc.player.posZ);
					speed = mc.player.motionX;
					break;

				case SOUTH:
					offset = 1 - (mc.player.posX - Math.floor(mc.player.posX));
					speed = mc.player.motionZ;
					break;

				case WEST:
					offset = 1 - (mc.player.posZ - Math.floor(mc.player.posZ));
					speed = mc.player.motionX;
					break;
				default:
					break;
				}

				speed = Math.abs(speed);

				float smoothFactor = 0.5f;
				float targetOffset = 0.5f;

				offset += (targetOffset - offset) * smoothFactor;

				if (speed < 0.1 && Math.abs(offset - 0.5) < 0.4 && placeDelay.getSecondValue().intValue() <= 1) {
				} else if (offset < 0.5 + ((Math.random() - 0.5) / 10)) {
				    mc.gameSettings.keyBindLeft.setPressed(false);
				    mc.gameSettings.keyBindRight.setPressed(true);
				} else {
				    mc.gameSettings.keyBindRight.setPressed(false);
				    mc.gameSettings.keyBindLeft.setPressed(true);
				}
			}

			break;

		case "Legit":
	        mc.entityRenderer.getMouseOver(1);
			float yaw = (mc.player.rotationYaw + 10000000) % 360;
			float staticYaw = (yaw - 180) - (yaw % 90) + 45;
			float staticPitch = 79;

			boolean straight = (Math.min(Math.abs(yaw % 90), Math.abs(90 - yaw) % 90) < Math
					.min(Math.abs(yaw + 45) % 90, Math.abs(90 - (yaw + 45)) % 90));

			if (straight
					&& RayCastUtil.rayCast(new Vector2f(staticYaw + 90, staticPitch),
							3).typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
					&& RayCastUtil.rayCast(new Vector2f(staticYaw, staticPitch),
							3).typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
				staticYaw += 90;

			}

			movementFix = MovementFix.SILENT;

			if (!straight) {
				staticYaw += 90;
			}
			
			targetYaw = staticYaw + yawDrift / 2;
			targetPitch = staticPitch + pitchDrift / 2;
			break;

		case "Telly":
			if (recursion == 0) {
				int time = mc.player.offGroundTicks;

				if (time == 2 || time == 0)
					mc.rightClickMouse();

				if (time >= 3 && mc.player.offGroundTicks <= (sameYValue.is("Off") ? 7 : 10)) {
					if (!RayCastUtil.overBlock(RotationComponent.rotations, enumFacing.getEnumFacing(), blockFace,
							rayCast.is("Strict"))) {
						getRotations(0);
					}
				} else {
					getRotations(Integer.parseInt(String.valueOf(this.yawOffset.getValue().getName())));
					targetYaw = mc.player.rotationYaw;
				}

				if (mc.player.offGroundTicks <= 3) {
					canPlace = false;
				}
			}
			break;

		case "Godbridge":
			if (getComponent(Slot.class).getItem() instanceof ItemBlock && canPlace) {
				mc.rightClickMouse();
			}

			targetYaw = (mc.player.rotationYaw - mc.player.rotationYaw % 90) - 180
					+ 45 * (mc.player.rotationYaw > 0 ? 1 : -1);
			targetPitch = 76.4f;

			movementFix = MovementFix.STRICT;

			double spacing = 0.15;
			boolean edgeX = Math.abs(mc.player.posX % 1) > 1 - spacing || Math.abs(mc.player.posX % 1) < spacing;
			boolean edgeZ = Math.abs(mc.player.posZ % 1) > 1 - spacing || Math.abs(mc.player.posZ % 1) < spacing;

			mc.gameSettings.keyBindRight.setPressed((edgeX && edgeZ) || (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())));
			mc.gameSettings.keyBindBack.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
			mc.gameSettings.keyBindForward.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
			mc.gameSettings.keyBindLeft.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));

			directionalChange++;
			if (Math.abs(
					MathHelper.wrapAngleTo180_double(targetYaw - RotationComponent.lastServerRotations.getX())) > 10) {
				directionalChange = (int) (Math.random() * 4);
				yawDrift = (float) (Math.random() - 0.5) / 10f;
				pitchDrift = (float) (Math.random() - 0.5) / 10f;
			}

			if (Math.random() > 0.99) {
				yawDrift = (float) (Math.random() - 0.5) / 10f;
				pitchDrift = (float) (Math.random() - 0.5) / 10f;
			}

			if (directionalChange <= 10) {
				mc.gameSettings.keyBindSneak.setPressed(true);
			} else if (directionalChange == 11) {
				mc.gameSettings.keyBindSneak.setPressed(false);
			}

			targetYaw += yawDrift;
			targetPitch += pitchDrift;
			break;
		}

		if (rotationSpeed != 0 && blockFace != null && enumFacing != null) {
			RotationComponent.setRotations(new Vector2f(targetYaw, targetPitch), rotationSpeed, movementFix);
		}
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		this.offset = new Vec3i(0, 0, 0);

		if (targetBlock == null || enumFacing == null || blockFace == null) {
			return;
		}

		mc.player.hideSneakHeight.reset();

		// Timer
		if (timer.getValue().floatValue() != 1)
			mc.timer.timerSpeed = timer.getValue().floatValue();
	};

	public void runMode() {
		if (this.mode.is("Telly")) {
			if (mc.player.onGround && MoveUtil.isMoving()) {
				mc.player.jump();
			}
		}
	}

	@EventLink
	public final Listener<TeleportEvent> onTeleport = event -> {
		if (event.getPosY() < mc.player.posY - 2)
			this.toggle();
	};
	
	@EventLink
	public final Listener<PreUpdateEvent> onPre = event -> {
		for (recursion = 0; recursion <= recursions; recursion++) {
			mc.player.safeWalk = this.safeWalk.getValue();

			resetBinds(false, false, true, true, false, false);

			if (expand.getValue().intValue() != 0) {
				double direction = MoveUtil.direction(mc.player.rotationYaw, mc.gameSettings.keyBindForward.isKeyDown() ? 1 : mc.gameSettings.keyBindBack.isKeyDown() ? -1 : 0, mc.gameSettings.keyBindRight.isKeyDown() ? -1 : mc.gameSettings.keyBindLeft.isKeyDown() ? 1 : 0);
				for (int range = 0; range <= expand.getValue().intValue(); range++) {
					if (PlayerUtil.blockAheadOfPlayer(range, this.offset.getY() - 0.5) instanceof BlockAir) {
						this.offset = this.offset.add(new Vec3i((int) (-Math.sin(direction) * (range + 1)), 0, (int) (Math.cos(direction) * (range + 1))));
						break;
					}
				}
			}

			// Same Y
			sameY = ((!this.sameYValue.is("Off") || this.getModule(Speed.class).isEnabled()) && !mc.gameSettings.keyBindJump.isKeyDown()) && MoveUtil.isMoving();

			// Getting ItemSlot
			getComponent(Slot.class).setSlotDelayed(SlotUtil.findBlock(), mc.player.offGroundTicks > 5 || ticksOnAir > 0 || sprint.is("Normal")); // 5

			// Used to detect when to place a block, if over air, allow placement of blocks
			if (doesNotContainBlock(1) && (!sameY || (doesNotContainBlock(2) && doesNotContainBlock(3) && doesNotContainBlock(4)))) {
				ticksOnAir++;
			} else {
				ticksOnAir = 0;
			}

			canPlace = !BadPacketsComponent.bad(false, true, false, false, true) && ticksOnAir > MathUtil.getRandom(placeDelay.getValue().intValue(), placeDelay.getSecondValue().intValue());

			if (recursion == 0)
				this.calculateSneaking();

			// Gets block to place
			targetBlock = PlayerUtil.getPlacePossibility(offset.getX(), offset.getY(), offset.getZ(),
					sameY ? (int) Math.floor(startY) : null);

			if (targetBlock == null) {
				return;
			}
			
			// Gets EnumFacing
			enumFacing = PlayerUtil.getEnumFacing(targetBlock, offset.getY() < 0);

			if (enumFacing == null) {
				return;
			}

			final BlockPos position = new BlockPos(targetBlock.xCoord, targetBlock.yCoord, targetBlock.zCoord);

			blockFace = position.add(enumFacing.getOffset().xCoord, enumFacing.getOffset().yCoord, enumFacing.getOffset().zCoord);
			
			if (blockFace == null || enumFacing == null || enumFacing.getEnumFacing() == null) {
				return;
			}
			
			this.calculateRotations();

			if (targetBlock == null || enumFacing == null || blockFace == null) {
				return;
			}

			if (startY - 1 != Math.floor(targetBlock.yCoord) && sameY) {
				return;
			}

			if (getComponent(Slot.class).getItemStack() == null || !(getComponent(Slot.class).getItemStack().getItem() instanceof ItemBlock)) {
				return;
			}

			// if (mc.player.offGroundTicks > 7) mc.rightClickMouse();

			if (getComponent(Slot.class).getItem() instanceof ItemBlock) {
				if (canPlace && (RayCastUtil.overBlock(enumFacing.getEnumFacing(), blockFace, rayCast.is("Strict")) || rayCast.is("Off"))) {
					this.place();

					// mc.rightClickDelayTimer = 0;
					ticksOnAir = 0;

					assert getComponent(Slot.class).getItemStack() != null;

					if (getComponent(Slot.class).getItemStack() != null && getComponent(Slot.class).getItemStack().stackSize == 0) {
						mc.player.inventory.mainInventory[getComponent(Slot.class).getItemIndex()] = null;
					}

				} else if (Math.random() > 0.3 && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit != null && mc.objectMouseOver.sideHit == EnumFacing.UP && rayCast.is("Strict") && !(PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir)) {
					mc.rightClickMouse();
				}
			}

			// For Same Y
			if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.posY % 1 > 0.5) {
				startY = Math.floor(mc.player.posY);
			}

			if ((mc.player.posY < startY || mc.player.onGround) && !MoveUtil.isMoving()) {
				startY = Math.floor(mc.player.posY);
			}
		}
	};

	public boolean doesNotContainBlock(int down) {
		return PlayerUtil.blockRelativeToPlayer(offset.getX(), -down + offset.getY(), offset.getZ())
				.isReplaceable(mc.world, new BlockPos(mc.player).down(down));
	}

	@EventLink
	public final Listener<MoveInputEvent> onMove = this::calculateSneaking;

	public Vec3 getHitVec() {
		/* Correct HitVec */
		Vec3 hitVec = new Vec3(blockFace.getX(), blockFace.getY(), blockFace.getZ());

		final MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(RotationComponent.rotations,
				mc.playerController.getBlockReachDistance());

		switch (enumFacing.getEnumFacing()) {
		case DOWN:
			hitVec.yCoord = blockFace.getY();
			break;

		case UP:
			hitVec.yCoord = blockFace.getY();
			break;

		case NORTH:
			hitVec.zCoord = blockFace.getZ();
			break;

		case EAST:
			hitVec.xCoord = blockFace.getX();
			break;

		case SOUTH:
			hitVec.zCoord = blockFace.getZ();
			break;

		case WEST:
			hitVec.xCoord = blockFace.getX();
			break;
		}

		if (movingObjectPosition != null && movingObjectPosition.getBlockPos() != null
				&& movingObjectPosition.hitVec != null && movingObjectPosition.getBlockPos().equals(blockFace)
				&& movingObjectPosition.sideHit == enumFacing.getEnumFacing()) {
			hitVec = movingObjectPosition.hitVec;
		}

		return hitVec;
	}

	private void place() {
		if (pause > 3)
			return;

		Vec3 hitVec = this.getHitVec();

		if (rayCast.is("Strict")) {
			mc.rightClickMouse();
		} else if (mc.playerController.onPlayerRightClick(mc.player, mc.world, getComponent(Slot.class).getItemStack(),
				blockFace, enumFacing.getEnumFacing(), hitVec)) {
			//mc.player.swingItem();
			PacketUtil.send(new C0APacketAnimation());
		}
	}

	public void getRotations(final int yawOffset) {
		EntityPlayer player = mc.player;
		double difference = player.posY + player.getEyeHeight() - targetBlock.yCoord - 0.5
				- (Math.random() - 0.5) * 0.1;

		MovingObjectPosition movingObjectPosition = null;		
		for (int offset = -180 + yawOffset; offset <= 180; offset += yawOffsetRandomization.getValue().intValue()) {
			player.setPosition(player.posX, player.posY - difference, player.posZ);
			movingObjectPosition = RayCastUtil.rayCast(new Vector2f((float) (player.rotationYaw + (offset * 3)), 0),
					20);
			player.setPosition(player.posX, player.posY + difference, player.posZ);

			if (movingObjectPosition == null || movingObjectPosition.hitVec == null)
				return;

			Vector2f rotations = RotationUtil.calculate(movingObjectPosition.hitVec);

			if (RayCastUtil.overBlock(rotations, blockFace, enumFacing.getEnumFacing())) {
				targetYaw = rotations.x;
				targetPitch = rotations.y;
				return;
			}
		}

		// Backup Rotations
		final Vector2f rotations = RotationUtil.calculate(
				new Vector3d(blockFace.getX(), blockFace.getY(), blockFace.getZ()), enumFacing.getEnumFacing());

		if (!RayCastUtil.overBlock(new Vector2f(targetYaw, targetPitch), blockFace, enumFacing.getEnumFacing())) {
			targetYaw = rotations.x;
			targetPitch = rotations.y;
		}
	}

	@EventLink
	public final Listener<PreStrafeEvent> onStrafe = event -> {
		this.runMode();

		if (!Objects.equals(yawOffset.getValue().getName(), "0") && !movementCorrection.getValue()) {
			MoveUtil.useDiagonalSpeed();
		}

		if (this.sameYValue.is("Auto Jump")) {
			if (mc.player.onGround && MoveUtil.isMoving() && mc.player.posY == startY) {
				mc.player.jump();
			}
		}
	};

	@EventLink
	public final Listener<PacketEvent> onPacketSend = event -> {
		Packet<?> packet = event.getPacket();
	    if (!event.isSend()) return;

		if (packet instanceof C08PacketPlayerBlockPlacement) {
			C08PacketPlayerBlockPlacement wrapper = (C08PacketPlayerBlockPlacement) packet;

			if (!wrapper.getPosition().equalsVector(new Vector3d(-1, -1, -1))) {
				placements--;
			}
		}
	};
}