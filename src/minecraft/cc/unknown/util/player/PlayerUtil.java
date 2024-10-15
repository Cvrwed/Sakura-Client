package cc.unknown.util.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.module.impl.combat.AutoClicker;
import cc.unknown.util.Accessor;
import cc.unknown.util.math.MathUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.util.vector.Vector2f;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraft.util.Vector3d;

@UtilityClass
public class PlayerUtil implements Accessor {
	private final HashMap<Integer, Integer> GOOD_POTIONS = new HashMap<Integer, Integer>() {
		{
			put(6, 1); // Instant Health
			put(10, 2); // Regeneration
			put(11, 3); // Resistance
			put(21, 4); // Health Boost
			put(22, 5); // Absorption
			put(23, 6); // Saturation
			put(5, 7); // Strength
			put(1, 8); // Speed
			put(12, 9); // Fire Resistance
			put(14, 10); // Invisibility
			put(3, 11); // Haste
			put(13, 12); // Water Breathing
		}
	};

	public final List<Block> BLOCK_BLACKLIST = Arrays.asList(Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest,
			Blocks.trapped_chest, Blocks.anvil, Blocks.sand, Blocks.web, Blocks.torch, Blocks.crafting_table,
			Blocks.furnace, Blocks.waterlily, Blocks.dispenser, Blocks.stone_pressure_plate,
			Blocks.wooden_pressure_plate, Blocks.noteblock, Blocks.dropper, Blocks.tnt, Blocks.standing_banner,
			Blocks.wall_banner, Blocks.redstone_torch);

	/**
	 * Gets the block at a position
	 *
	 * @return block
	 */
	public Block block(final double x, final double y, final double z) {
		return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
	}

	/**
	 * Gets the block at a position
	 *
	 * @return block
	 */
	public Block block(final BlockPos blockPos) {
		return mc.world.getBlockState(blockPos).getBlock();
	}

	public Block block(final Vec3i pos) {
		return block(new BlockPos(pos));
	}

	public boolean isOnEdge() {
		double posX = mc.player.posX;
		double posZ = mc.player.posZ;

		double edgeThreshold = -2.0;

		boolean onEdgeX = (posX % 1 < edgeThreshold || posX % 1 > (1 - edgeThreshold));
		boolean onEdgeZ = (posZ % 1 < edgeThreshold || posZ % 1 > (1 - edgeThreshold));

		return onEdgeX || onEdgeZ;
	}

	public Block block(final cc.unknown.util.vector.Vector3d pos) {
		return block(pos.getX(), pos.getY(), pos.getZ());
	}

	public Block block(final Vector3d pos) {
		return block(new BlockPos(new Vec3i(pos.field_181059_a, pos.field_181060_b, pos.field_181061_c)));
	}

	public boolean lookingAtBlock(final BlockPos blockFace, final float yaw, final float pitch,
			final EnumFacing enumFacing, final boolean strict) {
		final MovingObjectPosition movingObjectPosition = mc.player
				.rayTraceCustom(mc.playerController.getBlockReachDistance(), yaw, pitch);
		if (movingObjectPosition == null)
			return false;
		final Vec3 hitVec = movingObjectPosition.hitVec;
		if (hitVec == null)
			return false;
		if ((hitVec.xCoord - blockFace.getX()) > 1.0 || (hitVec.xCoord - blockFace.getX()) < 0.0)
			return false;
		if ((hitVec.yCoord - blockFace.getY()) > 1.0 || (hitVec.yCoord - blockFace.getY()) < 0.0)
			return false;
		return !((hitVec.zCoord - blockFace.getZ()) > 1.0) && !((hitVec.zCoord - blockFace.getZ()) < 0.0)
				&& (movingObjectPosition.sideHit == enumFacing || !strict);
	}

	public boolean keysDown() {
		return Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())
				|| Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())
				|| Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())
				|| Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
	}

	public float[] getDirectionToBlock(final double x, final double y, final double z, final EnumFacing enumfacing) {
		final EntityEgg var4 = new EntityEgg(mc.world);
		var4.posX = x + 0.5D;
		var4.posY = y + 0.5D;
		var4.posZ = z + 0.5D;
		var4.posX += (double) enumfacing.getDirectionVec().getX() * 0.5D;
		var4.posY += (double) enumfacing.getDirectionVec().getY() * 0.5D;
		var4.posZ += (double) enumfacing.getDirectionVec().getZ() * 0.5D;
		return getRotations(var4.posX, var4.posY, var4.posZ);
	}

	public float[] getRotations(final double posX, final double posY, final double posZ) {
		final EntityPlayerSP player = mc.player;
		final double x = posX - player.posX;
		final double y = posY - (player.posY + (double) player.getEyeHeight());
		final double z = posZ - player.posZ;
		final double dist = MathHelper.sqrt_double(x * x + z * z);
		final float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		final float pitch = (float) (-(Math.atan2(y, dist) * 180.0D / Math.PI));
		return new float[] { yaw, pitch };
	}

	/**
	 * Gets the distance between 2 positions
	 *
	 * @return distance
	 */
	public double distance(final BlockPos pos1, final BlockPos pos2) {
		final double x = pos1.getX() - pos2.getX();
		final double y = pos1.getY() - pos2.getY();
		final double z = pos1.getZ() - pos2.getZ();
		return x * x + y * y + z * z;
	}

	public double getFov(final double posX, final double posZ) {
		return getFov(mc.player.rotationYaw, posX, posZ);
	}

	public double getFov(final float yaw, final double posX, final double posZ) {
		double angle = (yaw - angle(posX, posZ)) % 360.0;
		return MathHelper.wrapAngleTo180_double(angle);
	}

	public float angle(final double n, final double n2) {
		return (float) (Math.atan2(n - mc.player.posX, n2 - mc.player.posZ) * 57.295780181884766 * -1.0);
	}

	/**
	 * Gets the block relative to the player from the offset
	 *
	 * @return block relative to the player
	 */
	public Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
		return block(mc.player.posX + offsetX, mc.player.posY + offsetY, mc.player.posZ + offsetZ);
	}

	public Block blockAheadOfPlayer(final double offsetXZ, final double offsetY) {
		return blockRelativeToPlayer(-Math.sin(MoveUtil.direction()) * offsetXZ, offsetY,
				Math.cos(MoveUtil.direction()) * offsetXZ);
	}

	/**
	 * Gets another players' username without any formatting
	 *
	 * @return players username
	 */
	public String name(final EntityPlayer player) {
		return player.getName();
	}

	/**
	 * Gets the players' username without any formatting
	 *
	 * @return players username
	 */
	public String name() {
		return mc.player.getName();
	}

	public boolean isHotbarFull() {
		for (int i = 0; i <= 36; ++i) {
			ItemStack itemstack = mc.player.inventory.getStackInSlot(i);
			if (itemstack == null) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if another players' team is the same as the players' team
	 *
	 * @return same team
	 */
	public boolean sameTeam(final EntityLivingBase player) {
		if (player.getTeam() != null && mc.player.getTeam() != null) {
			final char c1 = player.getDisplayName().getFormattedText().charAt(1);
			final char c2 = mc.player.getDisplayName().getFormattedText().charAt(1);
			return c1 == c2;
		}
		return false;
	}

	/**
	 * Checks if there is a block under the player
	 *
	 * @return block under
	 */
	public boolean isBlockUnder(final double height) {
		return isBlockUnder(height, true);
	}

	public boolean isBlockUnder(final double height, final boolean boundingBox) {
		if (boundingBox) {
			final AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, -height, 0);

			if (!mc.world.getCollidingBoundingBoxes(mc.player, bb).isEmpty()) {
				return true;
			}
		} else {
			for (int offset = 0; offset < height; offset++) {
				if (PlayerUtil.blockRelativeToPlayer(0, -offset, 0).isFullBlock()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isBlockOver(final double height, final boolean boundingBox) {
		final AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, height / 2f, 0).expand(0,
				height - mc.player.height, 0);

		if (!mc.world.getCollidingBoundingBoxes(mc.player, bb).isEmpty()) {
			return true;
		}

		return false;
	}

	public boolean isOverAir() {
		return mc.world.isAirBlock(new BlockPos(MathHelper.floor_double(mc.player.posX),
				MathHelper.floor_double(mc.player.posY - 1.0D), MathHelper.floor_double(mc.player.posZ)));
	}

	public boolean isBlockUnder() {
		return isBlockUnder(10);
	}

	public double distanceToBlockUnder() {
		double distance = 0;

		for (int i = 0; i < 256; i++) {
			if (blockRelativeToPlayer(0, -i, 0).isFullBlock()) {
				distance = i;
				break;
			}
		}

		return distance;
	}

	/**
	 * Checks if a potion is good
	 *
	 * @return good potion
	 */
	public boolean goodPotion(final int id) {
		return GOOD_POTIONS.containsKey(id);
	}

	/**
	 * Gets a potions ranking
	 *
	 * @return potion ranking
	 */
	public int potionRanking(final int id) {
		return GOOD_POTIONS.getOrDefault(id, -1);
	}

	/**
	 * Checks if the player is in a liquid
	 *
	 * @return in liquid
	 */
	public boolean inLiquid() {
		return mc.player.isInWater() || mc.player.isInLava();
	}

	/**
	 * Fake damages the player
	 */
	public void fakeDamage() {
		mc.player.handleHealthUpdate((byte) 2);
		mc.ingameGUI.healthUpdateCounter = mc.ingameGUI.updateCounter + 20;
	}

	/**
	 * Checks if the player is near a block
	 *
	 * @return block near
	 */
	public boolean blockNear(final int range) {
		for (int x = -range; x <= range; ++x) {
			for (int y = -range; y <= range; ++y) {
				for (int z = -range; z <= range; ++z) {
					final Block block = blockRelativeToPlayer(x, y, z);

					if (!(block instanceof BlockAir)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean blockNear(final int range, final Material material) {
		for (int x = -range; x <= range; ++x) {
			for (int y = -range; y <= range; ++y) {
				for (int z = -range; z <= range; ++z) {
					final Block block = blockRelativeToPlayer(x, y, z);

					if (block.getMaterial().equals(material)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the player is inside a block
	 *
	 * @return inside block
	 */
	public boolean insideBlock() {
		if (mc.player.ticksExisted < 5) {
			return false;
		}

		final EntityPlayerSP player = PlayerUtil.mc.player;
		final WorldClient world = PlayerUtil.mc.world;
		final AxisAlignedBB bb = player.getEntityBoundingBox();
		for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
			for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
				for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
					final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
					final AxisAlignedBB boundingBox;
					if (block != null && !(block instanceof BlockAir)
							&& (boundingBox = block.getCollisionBoundingBox(world, new BlockPos(x, y, z),
									world.getBlockState(new BlockPos(x, y, z)))) != null
							&& player.getEntityBoundingBox().intersectsWith(boundingBox)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Sends a click to Minecraft legitimately
	 */
	public void sendClick(final int button, final boolean state) {
		final int keyBind = button == 0 ? mc.gameSettings.keyBindAttack.getKeyCode()
				: mc.gameSettings.keyBindUseItem.getKeyCode();

		KeyBinding.setKeyBindState(keyBind, state);

		if (state) {
			KeyBinding.onTick(keyBind);
		}
	}

	public boolean onLiquid() {
		boolean onLiquid = false;
		final AxisAlignedBB playerBB = PlayerUtil.mc.player.getEntityBoundingBox();
		final WorldClient world = PlayerUtil.mc.world;
		final int y = (int) playerBB.offset(0.0, -0.01, 0.0).minY;
		for (int x = MathHelper.floor_double(playerBB.minX); x < MathHelper.floor_double(playerBB.maxX) + 1; ++x) {
			for (int z = MathHelper.floor_double(playerBB.minZ); z < MathHelper.floor_double(playerBB.maxZ) + 1; ++z) {
				final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
				if (block != null && !(block instanceof BlockAir)) {
					if (!(block instanceof BlockLiquid)) {
						return false;
					}
					onLiquid = true;
				}
			}
		}
		return onLiquid;
	}

	public EnumFacingOffset getEnumFacing(final Vec3 position) {
		return getEnumFacing(position, false);
	}

	public EnumFacingOffset getEnumFacing(final Vec3 position, boolean downwards) {
		List<EnumFacingOffset> possibleFacings = new ArrayList<>();
		for (int z2 = -1; z2 <= 1; z2 += 2) {
			if (!(PlayerUtil.block(position.xCoord, position.yCoord, position.zCoord + z2).isReplaceable(mc.world,
					new BlockPos(position.xCoord, position.yCoord, position.zCoord + z2)))) {
				if (z2 < 0) {
					possibleFacings.add(new EnumFacingOffset(EnumFacing.SOUTH, new Vec3(0, 0, z2)));
				} else {
					possibleFacings.add(new EnumFacingOffset(EnumFacing.NORTH, new Vec3(0, 0, z2)));
				}
			}
		}

		for (int x2 = -1; x2 <= 1; x2 += 2) {
			if (!(PlayerUtil.block(position.xCoord + x2, position.yCoord, position.zCoord).isReplaceable(mc.world,
					new BlockPos(position.xCoord + x2, position.yCoord, position.zCoord)))) {
				if (x2 > 0) {
					possibleFacings.add(new EnumFacingOffset(EnumFacing.WEST, new Vec3(x2, 0, 0)));
				} else {
					possibleFacings.add(new EnumFacingOffset(EnumFacing.EAST, new Vec3(x2, 0, 0)));
				}
			}
		}

		possibleFacings.sort(Comparator.comparingDouble(enumFacing -> {
			double enumFacingRotations = Math
					.toDegrees(Math.atan2(enumFacing.getOffset().zCoord, enumFacing.getOffset().xCoord)) % 360;
			double rotations = RotationComponent.rotations.x % 360 + 90;

			return Math.abs(MathUtil.wrappedDifference(enumFacingRotations, rotations));
		}));

		if (!possibleFacings.isEmpty())
			return possibleFacings.get(0);

		for (int y2 = -1; y2 <= 1; y2 += 2) {
			if (!(PlayerUtil.block(position.xCoord, position.yCoord + y2, position.zCoord).isReplaceable(mc.world,
					new BlockPos(position.xCoord, position.yCoord + y2, position.zCoord)))) {
				if (y2 < 0) {
					return new EnumFacingOffset(EnumFacing.UP, new Vec3(0, y2, 0));
				} else if (downwards) {
					return new EnumFacingOffset(EnumFacing.DOWN, new Vec3(0, y2, 0));
				}
			}
		}

		return null;
	}

	public Vec3 getPlacePossibility(double offsetX, double offsetY, double offsetZ) {
		return getPlacePossibility(offsetX, offsetY, offsetZ, null);
	}

	// This methods purpose is to get block placement possibilities, blocks are 1
	// unit thick so please don't change it to 0.5 it causes bugs.
	public Vec3 getPlacePossibility(double offsetX, double offsetY, double offsetZ, Integer plane) {

		final List<Vec3> possibilities = new ArrayList<>();
		final int range = (int) (5 + (Math.abs(offsetX) + Math.abs(offsetZ)));

		for (int x = -range; x <= range; ++x) {
			for (int y = -range; y <= range; ++y) {
				for (int z = -range; z <= range; ++z) {
					final Block block = blockRelativeToPlayer(x, y, z);

					if (!block.isReplaceable(mc.world,
							new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z))) {
						for (int x2 = -1; x2 <= 1; x2 += 2)
							possibilities
									.add(new Vec3(mc.player.posX + x + x2, mc.player.posY + y, mc.player.posZ + z));

						for (int y2 = -1; y2 <= 1; y2 += 2)
							possibilities
									.add(new Vec3(mc.player.posX + x, mc.player.posY + y + y2, mc.player.posZ + z));

						for (int z2 = -1; z2 <= 1; z2 += 2)
							possibilities
									.add(new Vec3(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z + z2));
					}
				}
			}
		}

		possibilities.removeIf(vec3 -> mc.player.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord) > 5
				|| !(PlayerUtil.block(vec3.xCoord, vec3.yCoord, vec3.zCoord).isReplaceable(mc.world,
						new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord))));

		if (possibilities.isEmpty())
			return null;

		if (plane != null) {
			possibilities.removeIf(vec3 -> Math.floor(vec3.yCoord + 1) != plane);
		}

		possibilities.sort(Comparator.comparingDouble(vec3 -> {

			final double d0 = (mc.player.posX + offsetX) - vec3.xCoord;
			final double d1 = (mc.player.posY - 1 + offsetY) - vec3.yCoord;
			final double d2 = (mc.player.posZ + offsetZ) - vec3.zCoord;
			return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);

		}));

		return possibilities.isEmpty() ? null : possibilities.get(0);
	}

	public boolean jumpDown() {
		return Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
	}

	public Vec3 getPlacePossibility() {
		return getPlacePossibility(0, 0, 0);
	}

	// Yes this is gay, yes I should use the clone method, but I'm doing it this way
	// anyway
	public EntityOtherPlayerMP getCopyOfPlayer(EntityOtherPlayerMP entityLivingBase) {
		EntityOtherPlayerMP entity = new EntityOtherPlayerMP(entityLivingBase.getEntityWorld(),
				entityLivingBase.getGameProfile());

		entity.motionX = entityLivingBase.motionX;
		entity.motionY = entityLivingBase.motionY;
		entity.motionZ = entityLivingBase.motionZ;
		entity.rotationYaw = entityLivingBase.rotationYaw;
		entity.setEntityId(entityLivingBase.getEntityId());

		entity.lastTickPosX = entityLivingBase.lastTickPosX;
		entity.lastTickPosY = entityLivingBase.lastTickPosY;
		entity.lastTickPosZ = entityLivingBase.lastTickPosZ;

		entity.setPosition(entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ);

		return entity;
	}

	public double calculatePerfectRangeToEntity(Entity entity) {
		double range = 1000;
		Vec3 eyes = mc.player.getPositionEyes(1);
		Vector2f rotations = RotationUtil.calculate(entity);
		final Vec3 rotationVector = mc.player.getVectorForRotation(rotations.getY(), rotations.getX());
		MovingObjectPosition movingObjectPosition = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1)
				.calculateIntercept(eyes, eyes.addVector(rotationVector.xCoord * range, rotationVector.yCoord * range,
						rotationVector.zCoord * range));

		return movingObjectPosition.hitVec.distanceTo(eyes);
	}

	public double fovFromEntity(EntityPlayer en) {
		return ((((double) (mc.player.rotationYaw - fovToEntity(en)) % 360.0D) + 540.0D) % 360.0D) - 180.0D;
	}

	public boolean inFov(float fov, Entity entity) {
		return inFov(fov, entity.posX, entity.posZ);
	}

	public boolean inFov(float fov, final double n2, final double n3) {
		fov *= 0.5F;
		final double fovToPoint = getFov(n2, n3);
		if (fovToPoint > 0.0) {
			return fovToPoint < fov;
		} else
			return fovToPoint > -fov;
	}

	public boolean inFov(float yaw, float fov, final double n2, final double n3) {
		fov *= 0.5F;
		final double fovToPoint = getFov(yaw, n2, n3);
		if (fovToPoint > 0.0) {
			return fovToPoint < fov;
		} else
			return fovToPoint > -fov;
	}

	public boolean inFov(float fov, Entity self, Entity target) {
		return inFov(self.rotationYaw, fov, target.posX, target.posZ);
	}

	public float fovToEntity(EntityPlayer ent) {
		double x = ent.posX - mc.player.posX;
		double z = ent.posZ - mc.player.posZ;
		double yaw = Math.atan2(x, z) * 57.2957795D;
		return (float) (yaw * -1.0D);
	}

	public boolean isHoldingWeapon() {
		if (mc.player.getCurrentEquippedItem() == null) {
			return false;
		} else {
			Item item = mc.player.getCurrentEquippedItem().getItem();
			return item instanceof ItemSword;
		}
	}

	public boolean isTeam(final EntityPlayer e, final EntityPlayer e2) {
		if (e2.getTeam() != null && e.getTeam() != null) {
			Character target = e2.getDisplayName().getFormattedText().charAt(1);
			Character player = e.getDisplayName().getFormattedText().charAt(1);
			if (target.equals(player)) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean isTeam(EntityPlayer entity, boolean scoreboard, boolean checkColor) {
		String entityName = entity.getDisplayName().getUnformattedText();
		String playerName = mc.player.getDisplayName().getUnformattedText();

		if (entityName.length() >= 3 && playerName.startsWith(entityName.substring(0, 3))) {
			return true;
		}

		if (mc.player.isOnSameTeam((EntityLivingBase) entity)) {
			return true;
		}

		if (scoreboard && mc.player.getTeam() != null && entity.getTeam() != null
				&& mc.player.getTeam().isSameTeam(entity.getTeam())) {
			return true;
		}

		if (checkColor && playerName != null && entity.getDisplayName() != null) {
			String targetName = entity.getDisplayName().getFormattedText().replace("§r", "");
			String clientName = playerName.replace("§r", "");
			return targetName.startsWith("§" + clientName.charAt(1));
		}

		return false;
	}

	public Vec3 getEyePos(Entity entity, Vec3 position) {
		return position.add(new Vec3(0, entity.getEyeHeight(), 0));
	}

	public Vec3 getEyePos(Entity entity) {
		return getEyePos(entity, new Vec3(entity));
	}

	public Vec3 getEyePos() {
		return getEyePos(mc.player);
	}

	public boolean isHoldingFood() {
		if (mc.player.getHeldItem() == null)
			return false;
		if (!(mc.player.getHeldItem().getItem() instanceof ItemFood)
				&& !(mc.player.getHeldItem().getItem() instanceof ItemBucketMilk))
			if (mc.player.getHeldItem().getItem() instanceof ItemPotion) {
				mc.player.getHeldItem().getItem();
				if (ItemPotion.isSplash(mc.player.getHeldItem().getMetadata()))
					return false;
			} else {
				return false;
			}
		return true;
	}

	public boolean isUsingItemB() {
		if (mc.player.isUsingItem())
			return true;
		if (mc.player.isEating())
			return true;
		if (mc.currentScreen != null)
			return false;
		return mc.gameSettings.keyBindUseItem.pressed;
	}

	public boolean isClicking() {
		AutoClicker clicker = Sakura.instance.getModuleManager().get(AutoClicker.class);

		if (clicker.isEnabled()) {
			return Mouse.isButtonDown(0);
		} else
			return Mouse.isButtonDown(0) && clicker != null && !clicker.isEnabled();
	}
}