package cc.unknown.module.impl.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.FriendAndTargetComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.util.tuples.Doble;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Tick Range", description = "Freezes minecraft instance to get closer to your target", category = Category.COMBAT)
public class TickRange extends Module {
	
	public final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Legit"))
			.add(new SubMode("Blatant"))
			.setDefault("Legit");
	
    private final NumberValue delay = new NumberValue("Delay", this, 2000, 500, 10000, 100, () -> !mode.is("Legit"));
    private final BoundsNumberValue range = new BoundsNumberValue("Range", this, 3.6, 5, 0, 15, 0.1, () -> !mode.is("Legit"));
    private final NumberValue lagTime = new NumberValue("Lag time", this, 150, 0, 500, 10, () -> !mode.is("Legit"));
    private final BooleanValue onlyGround = new BooleanValue("Only ground", this, false, () -> !mode.is("Legit"));
    private final BooleanValue ignoreTeams = new BooleanValue("Ignore teams", this, false, () -> !mode.is("Legit"));
    
    private final NumberValue coolDown = new NumberValue("Delay after dash to be able again to dash", this, 1, 1, 8, 0.5, () -> !mode.is("Blatant"));
    private final NumberValue range2 = new NumberValue("Distance from target to start dashing", this, 3, 3, 6, 0.1, () -> !mode.is("Blatant"));
    private final NumberValue freeze = new NumberValue("Freeze ticks duration on dash", this, 2, 1, 70, 1, () -> !mode.is("Blatant"));
    private final NumberValue packets = new NumberValue("Packets value to send on freeze", this, 2, 1, 70, 1, () -> !mode.is("Blatant"));

    public int durationTicks = 0, waitTicks = 0, delayTicks = 0;
    private List<TargetData> targetList = new ArrayList<TargetData>();
    public static boolean publicFreeze = false;
    private long lastLagTime = 0L;

    @Override
    public void onEnable() {
        clear();
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        publicFreeze = false;
        clear();
        super.onDisable();
    }
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
    	if (mode.is("Legit")) {
	        if (!shouldStart()) {
	            return;
	        }
	        
	        try {
	        	Thread.sleep(lagTime.getValue().intValue());                
	        } catch (InterruptedException e) {
	        	e.printStackTrace();
	        }
	        lastLagTime = System.currentTimeMillis();
    	}
    	
    	if (mode.is("Blatant")) {
            if(mc.world == null || mc.player == null) return;
            publicFreeze = false;

            if(waitTicks == 0){
                waitTicks--;
                for(int i = 0;i < packets.getValue().intValue() * 2.5;i++) {
                    mc.world.tick();
                }
            }
            if(waitTicks > 0){
                waitTicks --;
                publicFreeze = true;
            }else{
            }
            if(delayTicks > 0){

                delayTicks --;
            }
            if (!this.targetList.isEmpty()) {
                AxisAlignedBB afterBB = targetList.get(0).getTargetEntity().getEntityBoundingBox();
                double afterRange = RotationUtil.nearestRotation(afterBB);
                if(afterRange < range2.getValue().floatValue() && afterRange > 3 && mc.gameSettings.keyBindForward.pressed){
                    if(delayTicks > 0){
                    }else{
                        waitTicks = (int) (freeze.getValue().intValue() * 2.5);
                        delayTicks = (int) (coolDown.getValue().floatValue() * 160);
                    }
                }

            } else {
                clear();
                return;
            }
    	}
    };
    
    private boolean shouldStart() {
        if (!isInGame()) return false;
        if (getModule(Scaffold.class).isEnabled()) return false;
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
    
    public void clear() {
        publicFreeze = false;
        durationTicks = 0;
    }
    
    public class TargetData {
    	private String[] name;
    	private Entity targetEntity;

    	public TargetData(Entity entity) {
    		this.targetEntity = entity;
    	}

    	public Entity getTargetEntity() {
    		return this.targetEntity;
    	}

    	@Override
    	public boolean equals(Object obj) {
    		if (this == obj) {
    			return true;
    		}
    		if (obj instanceof TargetData) {
    			TargetData other = (TargetData) obj;
    			return other.getTargetEntity() == this.getTargetEntity();
    		}
    		return false;
    	}

    	@Override
    	public int hashCode() {
    		return targetEntity != null ? targetEntity.hashCode() : 0;
    	}
    }
}
