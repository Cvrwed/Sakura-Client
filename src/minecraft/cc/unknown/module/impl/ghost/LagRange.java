package cc.unknown.module.impl.ghost;

import java.awt.Color;
import java.util.Comparator;

import org.lwjgl.opengl.GL11;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.FriendAndTargetComponent;
import cc.unknown.component.impl.player.PingSpoofComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.BackTrack;
import cc.unknown.module.impl.combat.Criticals;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.util.tuples.Doble;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Lag Range", description = "Use lag help you to beat opponent.", category = Category.GHOST)
public class LagRange extends Module {
	
	private ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Tick"))
			.add(new SubMode("Latency"))
			.setDefault("Tick");
	
    private final NumberValue delay = new NumberValue("Delay", this, 2000, 500, 10000, 100, () -> !mode.is("Tick"));
    private final BoundsNumberValue range = new BoundsNumberValue("Range", this, 3.6, 5, 0, 15, 0.1);
    private final NumberValue lagTime = new NumberValue("Lag time", this, 150, 0, 500, 10, () -> !mode.is("Tick"));
    private final BooleanValue onlyGround = new BooleanValue("Only ground", this, true, () -> !mode.is("Tick"));
    private final BooleanValue ignoreTeams = new BooleanValue("Ignore teams", this, true, () -> !mode.is("Tick"));
    
    private final NumberValue everyMS = new NumberValue("Every Ms", this, 200, 200, 1000, 1, () -> !mode.is("Latency"));
    private final NumberValue delayMS = new NumberValue("Delay Ms", this, 200, 200, 1000, 1, () -> !mode.is("Latency"));
    public BooleanValue esp = new BooleanValue("Render ESP", this, true, () -> !mode.is("Latency"));
    private double x, y, z;
    private long lastLagTime = 0L;
    private boolean isBlinking = false;
    private boolean blinking = false, picked = false;
    private StopWatch stopWatch = new StopWatch(), stopWatch2 = new StopWatch();
    private EntityLivingBase target;
    
    @Override
    public void onEnable() {
        x = mc.player.posX;
        y = mc.player.posY;
        z = mc.player.posZ;
        blinking = false;
        super.onEnable();
    }
    
    @EventLink
    public final Listener<MotionEvent> onMotion = event -> {
    	if (event.isPre()) {
    		if (mode.is("Latency")) {
    			this.target = mc.world.getLoadedEntityList().stream()
    			        .filter(entity -> entity instanceof EntityPlayer && entity != mc.player)
    			        .map(entity -> (EntityPlayer) entity)
    			        .filter(entity -> (mc.player.getDistanceToEntity(entity) < range.getValue().doubleValue() || mc.player.getDistanceToEntity(entity) > range.getSecondValue().doubleValue()))
    			        .min(Comparator.comparingDouble(entity -> mc.player.getDistanceToEntity(entity)))
    			        .orElse(null);
	
	            if (stopWatch2.finished(everyMS.getValue().longValue())) {
	                blinking = true;
	            }
	
	            if (stopWatch.finished(delayMS.getValue().longValue()) && blinking) {
	                blinking = false;
	                stopWatch.reset();
	            }
	            
	            if (getModule(Scaffold.class).isEnabled() || getModule(Criticals.class).isEnabled() || getModule(BackTrack.class).isEnabled()) {
	            	return;
	            }
	
	            if (blinking) {
	                if (!picked) {
	                    x = mc.player.posX;
	                    y = mc.player.posY;
	                    z = mc.player.posZ;
	                    picked = true;
	                }
	
	                PingSpoofComponent.spoof(9999999, true, false, false, true, true);
	                stopWatch2.reset();
	            } else {
	                PingSpoofComponent.dispatch();
	                picked = false;
	            }
	    	}
    	}
    };
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        if (!shouldStart()) {
            return;
        }

        if (mode.is("Tick")) {
	        try {
	        	Thread.sleep(lagTime.getValue().intValue());
	        } catch (InterruptedException e) {
	        	e.printStackTrace();
	        }
	        lastLagTime = System.currentTimeMillis();
        }
        
        if (mode.is("Latency") || mode.is("Tick")) {
            if (!esp.getValue() || !blinking || mc.gameSettings.thirdPersonView == 0) {
                return;
            }
            
	        Color lineColor = Color.RED;
	        Color fillColor = ColorUtil.getAlphaColor(lineColor, 150);

            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDisable(3553);
            GlStateManager.disableCull();
            GL11.glDepthMask(false);
	        
            RenderUtil.drawPosESP(x, y, z, 0.23f, Color.RED, Color.RED);

            GL11.glDepthMask(true);
            GlStateManager.enableCull();
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDisable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2848);
        }
    };
    
    private boolean shouldStart() {
        if (!isInGame()) return false;
        if (onlyGround.getValue() && !mc.player.onGround) return false;
        if (!MoveUtil.isMoving()) return false;
        if (System.currentTimeMillis() - lastLagTime < delay.getValue().intValue()) return false;

        EntityPlayer target = mc.world.playerEntities.stream()
                .filter(p -> p != mc.player)
                .filter(p -> !ignoreTeams.getValue() || !PlayerUtil.isTeam(p, true, true))
                .filter(p -> !FriendAndTargetComponent.isFriend(p))
                .filter(p -> !Sakura.instance.getBotManager().contains(p))
                .map(p -> new Doble<>(p, mc.player.getDistanceSqToEntity(p)))
                .min(Comparator.comparing(Doble::getSecond))
                .map(Doble::getFirst)
                .orElse(null);

        if (target == null) return false;

        double distance = new Vec3(target).distanceTo(mc.player);
        return distance >= range.getValue().doubleValue() && distance <= range.getSecondValue().doubleValue();
    }
}
