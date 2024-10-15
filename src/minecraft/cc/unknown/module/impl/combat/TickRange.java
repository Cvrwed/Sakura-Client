package cc.unknown.module.impl.combat;

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
import cc.unknown.module.impl.latency.BackTrack;
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

@ModuleInfo(aliases = "Tick Range", description = "Use freeze help you to beat opponent.", category = Category.COMBAT)
public class TickRange extends Module {
	
    private final NumberValue delay = new NumberValue("Delay", this, 2000, 500, 10000, 100);
    private final BoundsNumberValue range = new BoundsNumberValue("Range", this, 3.6, 5, 0, 15, 0.1);
    private final NumberValue lagTime = new NumberValue("Lag time", this, 150, 0, 500, 10);
    private final BooleanValue onlyGround = new BooleanValue("Only ground", this, true);
    private final BooleanValue ignoreTeams = new BooleanValue("Ignore teams", this, true);

    private long lastLagTime = 0L;
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        if (!shouldStart()) {
            return;
        }
        
        if (getModule(Scaffold.class).isEnabled()) return;

        try {
        	Thread.sleep(lagTime.getValue().intValue());                
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
        lastLagTime = System.currentTimeMillis();
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
