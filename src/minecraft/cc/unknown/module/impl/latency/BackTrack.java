package cc.unknown.module.impl.latency;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.FriendAndTargetComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Back Track", description = "Uses latency to give you a reach advantage", category = Category.LATENCY)
public final class BackTrack extends Module {

	private final NumberValue range = new NumberValue("Range", this, 6, 3, 6, 0.1);
	private final NumberValue maxAngle = new NumberValue("Max Angle", this, 360, 15, 360, 5);
	private final NumberValue delay = new NumberValue("Delay", this, 450, 50, 5000, 50);
	private final NumberValue hitRange = new NumberValue("Post Range", this, 6, 3, 10, 0.1);
	private final BooleanValue tick = new BooleanValue("Reset Packets per Tick", this, false);
	private final NumberValue tickPackets = new NumberValue("Ticks", this, 1, 1, 10, 1, () -> !tick.getValue());
	private final BooleanValue esp = new BooleanValue("Extra Sensory Perception", this, true);
	private final BooleanValue onlyWhenNeed = new BooleanValue("Legitimize", this, true);
	private final BooleanValue whenDmg = new BooleanValue("Reset while receive damage", this, false);
	private final BooleanValue ignoreTeammates = new BooleanValue("Ignore Teams", this, false);

	private final StopWatch timeHelper = new StopWatch();
    private final ArrayList<Packet> packets = new ArrayList<>();
    private WorldClient lastWorld;
	private int realPosX;
	private int realPosY;
	private int realPosZ;
	
	@EventLink
	public final Listener<TickEvent> onGame = event -> {
	    INetHandler netHandler = mc.getNetHandler().getNetworkManager().packetListener;
		final EntityPlayer target = getEnemy();
	    if (netHandler instanceof OldServerPinger) return;

	    if (target == null || !isInGame() || netHandler == null) return;

	    double realX = realPosX / 32.0D;
	    double realY = realPosY / 32.0D;
	    double realZ = realPosZ / 32.0D;
	    double targetX = target.serverPosX / 32.0D;
	    double targetY = target.serverPosY / 32.0D;
	    double targetZ = target.serverPosZ / 32.0D;

	    AxisAlignedBB targetBoundingBox = new AxisAlignedBB(
	        targetX - target.width, targetY, targetZ - target.width,
	        targetX + target.width, targetY + target.height, targetZ + target.width
	    );

	    Vec3 positionEyes = mc.player.getPositionEyes(mc.timer.renderPartialTicks);
	    double currentX = MathHelper.clamp_double(positionEyes.xCoord, targetBoundingBox.minX, targetBoundingBox.maxX);
	    double currentY = MathHelper.clamp_double(positionEyes.yCoord, targetBoundingBox.minY, targetBoundingBox.maxY);
	    double currentZ = MathHelper.clamp_double(positionEyes.zCoord, targetBoundingBox.minZ, targetBoundingBox.maxZ);

	    AxisAlignedBB playerBoundingBox = new AxisAlignedBB(
	        realX - target.width, realY, realZ - target.width,
	        realX + target.width, realY + target.height, realZ + target.width
	    );

	    double realClosestX = MathHelper.clamp_double(positionEyes.xCoord, playerBoundingBox.minX, playerBoundingBox.maxX);
	    double realClosestY = MathHelper.clamp_double(positionEyes.yCoord, playerBoundingBox.minY, playerBoundingBox.maxY);
	    double realClosestZ = MathHelper.clamp_double(positionEyes.zCoord, playerBoundingBox.minZ, playerBoundingBox.maxZ);

	    double distance = hitRange.getValue().doubleValue();
	    if (!mc.player.canEntityBeSeen(target)) {
	        distance = Math.min(distance, 3.0D);
	    }

	    AxisAlignedBB entityBoundingBox = target.getEntityBoundingBox();
	    double bestX = MathHelper.clamp_double(positionEyes.xCoord, entityBoundingBox.minX, entityBoundingBox.maxX);
	    double bestY = MathHelper.clamp_double(positionEyes.yCoord, entityBoundingBox.minY, entityBoundingBox.maxY);
	    double bestZ = MathHelper.clamp_double(positionEyes.zCoord, entityBoundingBox.minZ, entityBoundingBox.maxZ);

	    boolean shouldReset = positionEyes.distanceTo(new Vec3(bestX, bestY, bestZ)) > 2.9 || (mc.player.hurtTime < 8 && mc.player.hurtTime > 1);

	    if (!onlyWhenNeed.getValue()) {
	        shouldReset = true;
	    }

	    double distanceToRealClosest = positionEyes.distanceTo(new Vec3(realClosestX, realClosestY, realClosestZ));
	    double distanceToCurrent = positionEyes.distanceTo(new Vec3(currentX, currentY, currentZ));

	    if (shouldReset && distanceToRealClosest > distanceToCurrent + 0.05 && mc.player.getDistance(realX, realY, realZ) < distance && timeHelper.reached((long) delay.getValue().longValue())) {
	        resetPackets();
	        resetPackets(netHandler);
	        timeHelper.reset();
	    }
	};

	@EventLink
	public final Listener<PacketEvent> onPacket = event -> {
	    Packet<?> packet = event.getPacket();
	    INetHandler netHandler = mc.getNetHandler().getNetworkManager().packetListener;
		final EntityPlayer target = getEnemy();
		
        if (target == null || shouldResetPackets()) {
            resetPackets();
            resetPackets(netHandler);
            return;
        }
		
	    if (event.isReceive()) {
	        if (netHandler != null && netHandler instanceof OldServerPinger) return;

	        if (mc.world != null) {
	            synchronized (this) {
	                handlePacket(packet);

	                if (isInGame() && tick.getValue()) {
	                    if (mc.player.ticksExisted % tickPackets.getValue().intValue() == 0) {
	                        resetPackets();
	                        resetPackets(netHandler);
	                        return;
	                    }
	                }

	                addPackets(packet, event);
	            }
	        }
	    }
	};

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		final EntityPlayer target = getEnemy();
		
		if (target == null || mc.getRenderManager() == null) return;
		
	    if (esp.getValue()) {
	    	final double x = realPosX / 32D - mc.getRenderManager().renderPosX;
	        final double y = realPosY / 32D - mc.getRenderManager().renderPosY;
	        final double z = realPosZ / 32D - mc.getRenderManager().renderPosZ;

	        boolean isHurt = target.hurtTime > 0;
	        	        
	        Color lineColor = isHurt ? Color.RED : Color.GREEN;
	        Color fillColor = ColorUtil.getAlphaColor(lineColor, 150);
	        	        
	        GL11.glPushMatrix();
	        GL11.glTranslated(x, y + target.height, z);
	        GL11.glNormal3d(0.0, 1.0, 0.0);
	        GL11.glRotated(90, 1, 0, 0);

	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glDisable(GL11.GL_DEPTH_TEST);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        GL11.glLineWidth(1);

	        ColorUtil.setColor(lineColor);
	        Cylinder cylinder = new Cylinder();
	        cylinder.setDrawStyle(GLU.GLU_LINE);
	        cylinder.setOrientation(GLU.GLU_INSIDE);
	        cylinder.draw(0.62f, 0.62f, target.height, 8, 1);

	        ColorUtil.setColor(fillColor);
	        cylinder.setDrawStyle(GLU.GLU_FILL);
	        cylinder.setOrientation(GLU.GLU_INSIDE);
	        cylinder.draw(0.62f, 0.65f, target.height, 8, 1);

	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glEnable(GL11.GL_DEPTH_TEST);
	        GL11.glPopMatrix();
	    }
	};

	private void resetPackets(INetHandler netHandler) {
        if (!packets.isEmpty()) {
            while (!packets.isEmpty()) {
                final Packet packet = packets.get(0);
                if (packet != null) {
                    try {
                        packet.processPacket(netHandler);
                    } catch (Exception e) {
                        //  e.printStackTrace();
                    }
                }

                try {
                    packets.remove(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
	}

    private void resetPackets() {
    }

    private void addPackets(Packet packet, PacketEvent eventReadPacket) {
        synchronized (packets) {
            if (blockPacket(packet)) {
                packets.add(packet);
                eventReadPacket.setCancelled(true);
            }
        }
    }
    
    private EntityPlayer getEnemy() {
        final int fov = maxAngle.getValue().intValue();
        final double maxRange = range.getValue().doubleValue();
        final List<EntityPlayer> players = mc.world.playerEntities;
        final Vec3 playerPos = new Vec3(mc.player);

        EntityPlayer target = null;
        double closestFov = Double.MAX_VALUE;

        for (EntityPlayer entityPlayer : players) {
            if (entityPlayer == mc.player || entityPlayer.deathTime != 0) continue;
            double dist = playerPos.distanceTo(entityPlayer);
            if (FriendAndTargetComponent.isTarget(entityPlayer) || Sakura.instance.getBotManager().contains(entityPlayer) || (ignoreTeammates.getValue() && PlayerUtil.isTeam(entityPlayer, true, true)) || dist > maxRange) continue;
            if (fov != 360 && !PlayerUtil.inFov(fov, entityPlayer)) continue;
            double curFov = Math.abs(PlayerUtil.getFov(entityPlayer.posX, entityPlayer.posZ));
            if (curFov < closestFov) {
                target = entityPlayer;
                closestFov = curFov;
            }
        }

        return target;
    }
    
	private void handlePacket(Packet<?> packet) {
	    if (packet instanceof S14PacketEntity) {
	        Entity entity = ((S14PacketEntity) packet).getEntity(mc.world);
	        if (entity instanceof EntityLivingBase) {
	            realPosX += ((S14PacketEntity) packet).getPosX();
	            realPosY += ((S14PacketEntity) packet).getPosY();
	            realPosZ += ((S14PacketEntity) packet).getPosZ();
	        }
	    }
	}

	private boolean shouldResetPackets() {
	    return (whenDmg.getValue() && mc.player.getHealth() < mc.player.getMaxHealth() && mc.player.hurtTime != 0) || (lastWorld != mc.world);
	}
    
    private boolean blockPacket(Packet<?> packet) {
        if (mc.currentScreen != null) {
            return false;

        } else if (packet instanceof S03PacketTimeUpdate) {
            return true;

        } else if (packet instanceof S00PacketKeepAlive) {
            return true;

        } else if (packet instanceof S12PacketEntityVelocity) {
            return true;

        } else if (packet instanceof S27PacketExplosion) {
            return true;

        } else if (packet instanceof S19PacketEntityStatus) {
            S19PacketEntityStatus entityStatus = (S19PacketEntityStatus) packet;
            return entityStatus.getOpCode() != 2 || !(mc.world.getEntityByID(entityStatus.entityId) instanceof EntityLivingBase);

        } else {
            return !(packet instanceof S06PacketUpdateHealth) && !(packet instanceof S29PacketSoundEffect) && !(packet instanceof S3EPacketTeams) && !(packet instanceof S0CPacketSpawnPlayer);
        }
    }
}