package cc.unknown.module.impl.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL11;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.FriendAndTargetComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Teleport Aura", description = "Rape ur enemies at long range", category = Category.COMBAT)
public class TeleportAura extends Module {

	private final NumberValue target = new NumberValue("Targets", this, 1, 1, 50, 1);
	private final NumberValue cps = new NumberValue("Clicks per second", this, 1, 1, 20, 1);
	private final NumberValue range = new NumberValue("Range", this, 8, 2, 100, 1);
	private final BooleanValue esp = new BooleanValue("Draw ESP", this, true);
	private final BooleanValue invi = new BooleanValue("Target invisibles", this, true);
	private final BooleanValue teams = new BooleanValue("Check if player is not on your team", this, false);

	private boolean canReach;
	private double dashDistance = 5;
    private ArrayList<Vec3> path = new ArrayList<>();
    private List<Vec3>[] test = new ArrayList[50];
	private List<EntityLivingBase> targets = new CopyOnWriteArrayList<>();
	private final StopWatch stopWatch = new StopWatch();
	private final StopWatch cpsTimer = new StopWatch();

	@Override
	public void onEnable() {
		stopWatch.reset();
		targets.clear();
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		 int delayValue = (20 / this.cps.getValue().intValue()) * 50;
		 targets = getTargets();
		 
		 if (cpsTimer.finished(delayValue))
			 if (targets.size() > 0) {
				 test = new ArrayList[50];
				 for (int i = 0; i < (targets.size() > target.getValue().intValue() ? target.getValue().intValue() : targets.size()); i++) {
					 EntityLivingBase T = targets.get(i);
					 Vec3 topFrom = new Vec3(mc.player.posX, mc.player.posY, mc.player.posZ);
					 Vec3 to = new Vec3(T.posX, T.posY, T.posZ);
                         
					 path = computePath(topFrom, to);
					 test[i] = path;
					 for (Vec3 pathElm : path) {
						 PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getXCoord(), pathElm.getYCoord(), pathElm.getZCoord(), true));
					 }

					 mc.player.swingItem();
					 mc.playerController.attackEntity(mc.player, T);
					 Collections.reverse(path);
					 for (Vec3 pathElm : path) {
						 PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getXCoord(), pathElm.getYCoord(), pathElm.getZCoord(), true));
					 }
				 }
				 cpsTimer.reset();
			 }
	};
	
	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
        if (!targets.isEmpty() && esp.getValue()) {
            if (targets.size() > 0) {
                for (int i = 0; i < (targets.size() > target.getValue().intValue() ? target.getValue().intValue() : targets.size()); i++) {
                    drawESP(targets.get(i), getTheme().getAccentColor().getRGB());
                }

            }
        }
        if (!path.isEmpty()) {
            for (int i = 0; i < targets.size(); i++) {
                try {
                    if (test != null)
                        for (Vec3 pos : test[i]) {
                            if (pos != null);
                        }
                } catch (Exception e) {

                }
            }

            if (cpsTimer.finished(1000)) {
                test = new ArrayList[50];
                path.clear();
            }
        }
	};
 
	private ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
		if (!canPassThrow(new BlockPos(topFrom.mc()))) {
			topFrom = topFrom.addVector(0, 1, 0);
		}
		AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
		pathfinder.compute();

		int i = 0;
		Vec3 lastLoc = null;
		Vec3 lastDashLoc = null;
		ArrayList<Vec3> path = new ArrayList<Vec3>();
		ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
		for (Vec3 pathElm : pathFinderPath) {
			if (i == 0 || i == pathFinderPath.size() - 1) {
				if (lastLoc != null) {
					path.add(lastLoc.addVector(0.5, 0, 0.5));
				}
				path.add(pathElm.addVector(0.5, 0, 0.5));
				lastDashLoc = pathElm;
			} else {
				boolean canContinue = true;
				if (pathElm.squareDistanceTo(lastDashLoc) > dashDistance * dashDistance) {
					canContinue = false;
				} else {
					double smallX = Math.min(lastDashLoc.getXCoord(), pathElm.getXCoord());
					double smallY = Math.min(lastDashLoc.getYCoord(), pathElm.getYCoord());
					double smallZ = Math.min(lastDashLoc.getZCoord(), pathElm.getZCoord());
					double bigX = Math.max(lastDashLoc.getXCoord(), pathElm.getXCoord());
					double bigY = Math.max(lastDashLoc.getYCoord(), pathElm.getYCoord());
					double bigZ = Math.max(lastDashLoc.getZCoord(), pathElm.getZCoord());
					cordsLoop: for (int x = (int) smallX; x <= bigX; x++) {
						for (int y = (int) smallY; y <= bigY; y++) {
							for (int z = (int) smallZ; z <= bigZ; z++) {
								if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
									canContinue = false;
									break cordsLoop;
								}
							}
						}
					}
				}
				if (!canContinue) {
					path.add(lastLoc.addVector(0.5, 0, 0.5));
					lastDashLoc = lastLoc;
				}
			}
			lastLoc = pathElm;
			i++;
		}
		return path;
	}

	private boolean canPassThrow(BlockPos pos) {
		Block block = mc.world
				.getBlockState(new net.minecraft.util.BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
		return block.getMaterial() == Material.air || block.getMaterial() == Material.plants
				|| block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water
				|| block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
	}

	boolean validEntity(EntityLivingBase entity) {
		float range = this.range.getValue().floatValue();

		if ((mc.player.isEntityAlive()) && !(entity instanceof EntityPlayerSP)) {
			if (mc.player.getDistanceToEntity(entity) <= range) {
				if (Sakura.instance.getBotManager().contains(entity)) {
					return false;
				}
				if (entity.isPlayerSleeping()) {
					return false;
				}
				if (FriendAndTargetComponent.isFriend(entity.getName())) {
					return false;
				}

				if (entity instanceof EntityPlayer) {

					EntityPlayer player = (EntityPlayer) entity;
					if (!player.isEntityAlive() && player.getHealth() == 0.0) {
						return false;
					} else if (PlayerUtil.isTeam(mc.player, player) && teams.getValue()) {
						return false;
					} else if (player.isInvisible() && !invi.getValue()) {
						return false;
					} else if (FriendAndTargetComponent.isFriend(player.getName())) {
						return false;
					} else
						return true;
					
				} else {
					if (!entity.isEntityAlive()) {

						return false;
					}
				}

				if (entity instanceof EntityMob) {
					return false;
				}
				if ((entity instanceof EntityAnimal || entity instanceof EntityVillager)) {
					if (entity.getName().equals("Villager")) {
						return false;
					}
					return false;
				}
			}
		}

		return false;
	}

	private List<EntityLivingBase> getTargets() {
		List<EntityLivingBase> targets = new ArrayList<>();

		for (Object o : mc.world.getLoadedEntityList()) {
			if (o instanceof EntityLivingBase) {
				EntityLivingBase entity = (EntityLivingBase) o;
				if (validEntity(entity)) {
					targets.add(entity);
				}
			}
		}
		targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.player) * 1000
				- o2.getDistanceToEntity(mc.player) * 1000));
		return targets;
	}

	public void drawESP(Entity entity, int color) {
		double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;

		double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;

		double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
		double width = Math.abs(entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX);
		double height = Math.abs(entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY);
		Vec3 vec = new Vec3(x - width / 2, y, z - width / 2);
		Vec3 vec2 = new Vec3(x + width / 2, y + height, z + width / 2);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
		RenderUtil.drawBoundingBox(
				new AxisAlignedBB(vec.getXCoord() - mc.getRenderManager().renderPosX, vec.getYCoord() - mc.getRenderManager().renderPosY,
						vec.getZCoord() - mc.getRenderManager().renderPosZ, vec2.getXCoord() - mc.getRenderManager().renderPosX,
						vec2.getYCoord() - mc.getRenderManager().renderPosY, vec2.getZCoord() - mc.getRenderManager().renderPosZ), getTheme().getAccentColor().getRed(), getTheme().getAccentColor().getGreen(), getTheme().getAccentColor().getBlue(), getTheme().getAccentColor().getAlpha());
		GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glColor4f(1, 1, 1, 1);
	}

	public static class AStarCustomPathFinder {
	    private Vec3 startVec3;
	    private Vec3 endVec3;
	    private ArrayList<Vec3> path = new ArrayList<Vec3>();
	    private ArrayList<Hub> hubs = new ArrayList<Hub>();
	    private ArrayList<Hub> hubsToWork = new ArrayList<Hub>();
	    private double minDistanceSquared = 9;
	    private boolean nearest = true;

	    private static Vec3[] flatCardinalDirections = {
	            new Vec3(1, 0, 0),
	            new Vec3(-1, 0, 0),
	            new Vec3(0, 0, 1),
	            new Vec3(0, 0, -1)
	    };

	    public AStarCustomPathFinder(Vec3 startVec3, Vec3 endVec3) {
	        this.startVec3 = startVec3.addVector(0, 0, 0).floor();
	        this.endVec3 = endVec3.addVector(0, 0, 0).floor();
	    }

	    public ArrayList<Vec3> getPath() {
	        return path;
	    }

	    public void compute() {
	        compute(1000, 4);
	    }

	    public void compute(int loops, int depth) {
	        path.clear();
	        hubsToWork.clear();
	        ArrayList<Vec3> initPath = new ArrayList<Vec3>();
	        initPath.add(startVec3);
	        hubsToWork.add(new Hub(startVec3, null, initPath, startVec3.squareDistanceTo(endVec3), 0, 0));
	        search:
	        for (int i = 0; i < loops; i++) {
	            Collections.sort(hubsToWork, new CompareHub());
	            int j = 0;
	            if (hubsToWork.size() == 0) {
	                break;
	            }
	            for (Hub hub : new ArrayList<Hub>(hubsToWork)) {
	                j++;
	                if (j > depth) {
	                    break;
	                } else {
	                    hubsToWork.remove(hub);
	                    hubs.add(hub);

	                    for (Vec3 direction : flatCardinalDirections) {
	                        Vec3 loc = hub.getLoc().add(direction).floor();
	                        if (checkPositionValidity(loc, false)) {
	                            if (addHub(hub, loc, 0)) {
	                                break search;
	                            }
	                        }
	                    }

	                    Vec3 loc1 = hub.getLoc().addVector(0, 1, 0).floor();
	                    if (checkPositionValidity(loc1, false)) {
	                        if (addHub(hub, loc1, 0)) {
	                            break search;
	                        }
	                    }

	                    Vec3 loc2 = hub.getLoc().addVector(0, -1, 0).floor();
	                    if (checkPositionValidity(loc2, false)) {
	                        if (addHub(hub, loc2, 0)) {
	                            break search;
	                        }
	                    }
	                }
	            }
	        }
	        if (nearest) {
	            Collections.sort(hubs, new CompareHub());
	            path = hubs.get(0).getPath();
	        }
	    }

	    public boolean checkPositionValidity(Vec3 loc, boolean checkGround) {
	        return checkPositionValidity((int) loc.getXCoord(), (int) loc.getYCoord(), (int) loc.getZCoord(), checkGround);
	    }

	    public static boolean checkPositionValidity(int x, int y, int z, boolean checkGround) {
	        BlockPos block1 = new BlockPos(x, y, z);
	        BlockPos block2 = new BlockPos(x, y + 1, z);
	        BlockPos block3 = new BlockPos(x, y - 1, z);
	        return !isBlockSolid(block1) && !isBlockSolid(block2) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
	    }

	    private static boolean isBlockSolid(BlockPos block) {
	        return mc.world.getBlock(block.getX(), block.getY(), block.getZ()).isSolidFullCube() ||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockSlab) ||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockStairs)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockCactus)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockChest)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockEnderChest)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockSkull)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockPane)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockFence)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockWall)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockGlass)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockPistonBase)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockPistonExtension)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockPistonMoving)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockStainedGlass)||
	        		(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockTrapDoor);
	    }

	    private static boolean isSafeToWalkOn(BlockPos block) {
	        return !(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockFence) && 
	        		!(mc.world.getBlock(block.getX(), block.getY(), block.getZ()) instanceof BlockWall);
	    }

	    public Hub isHubExisting(Vec3 loc) {
	        for (Hub hub : hubs) {
	            if (hub.getLoc().getXCoord() == loc.getXCoord() && hub.getLoc().getYCoord() == loc.getYCoord() && hub.getLoc().getZCoord() == loc.getZCoord()) {
	                return hub;
	            }
	        }
	        for (Hub hub : hubsToWork) {
	            if (hub.getLoc().getXCoord() == loc.getXCoord() && hub.getLoc().getYCoord() == loc.getYCoord() && hub.getLoc().getZCoord() == loc.getZCoord()) {
	                return hub;
	            }
	        }
	        return null;
	    }

	    public boolean addHub(Hub parent, Vec3 loc, double cost) {
	        Hub existingHub = isHubExisting(loc);
	        double totalCost = cost;
	        if (parent != null) {
	            totalCost += parent.getTotalCost();
	        }
	        if (existingHub == null) {
	            if ((loc.getXCoord() == endVec3.getXCoord() && loc.getYCoord() == endVec3.getYCoord() && loc.getZCoord() == endVec3.getZCoord()) || (minDistanceSquared != 0 && loc.squareDistanceTo(endVec3) <= minDistanceSquared)) {
	                path.clear();
	                path = parent.getPath();
	                path.add(loc);
	                return true;
	            } else {
	                ArrayList<Vec3> path = new ArrayList<Vec3>(parent.getPath());
	                path.add(loc);
	                hubsToWork.add(new Hub(loc, parent, path, loc.squareDistanceTo(endVec3), cost, totalCost));
	            }
	        } else if (existingHub.getCost() > cost) {
	            ArrayList<Vec3> path = new ArrayList<Vec3>(parent.getPath());
	            path.add(loc);
	            existingHub.setLoc(loc);
	            existingHub.setParent(parent);
	            existingHub.setPath(path);
	            existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(endVec3));
	            existingHub.setCost(cost);
	            existingHub.setTotalCost(totalCost);
	        }
	        return false;
	    }

	    @Getter
	    @Setter
	    @AllArgsConstructor
	    private class Hub {
	        private Vec3 loc = null;
	        private Hub parent = null;
	        private ArrayList<Vec3> path;
	        private double squareDistanceToFromTarget;
	        private double cost;
	        private double totalCost;
	    }

	    public class CompareHub implements Comparator<Hub> {
	        @Override
	        public int compare(Hub o1, Hub o2) {
	            return (int) (
	                    (o1.getSquareDistanceToFromTarget() + o1.getTotalCost()) - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost())
	            );
	        }
	    }
	}
}
