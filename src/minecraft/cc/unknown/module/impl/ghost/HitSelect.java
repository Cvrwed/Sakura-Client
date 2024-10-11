package cc.unknown.module.impl.ghost;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.PreUpdateEvent;
import cc.unknown.event.impl.other.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.Entity;

@ModuleInfo(aliases = "Hit Select", description = "Chooses the best time to hit.", category = Category.GHOST)
public class HitSelect extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Legitimate KeepSprint"))
			.add(new SubMode("KnockBack Reduction"))
			.add(new SubMode("Critical hit frequency"))
			.setDefault("KnockBack reduction");
	
    private final NumberValue delay = new NumberValue("Delay", this, 420, 50, 500, 1);
    private final NumberValue chance = new NumberValue("Chance", this, 80, 0, 100, 1);
    
    private long attackTime = 0;
    private boolean currentShouldAttack = false;
    
    @EventLink(value = Priority.VERY_LOW)
    public final Listener<AttackEvent> onAttackEvent = event -> {
        if (!currentShouldAttack) {
            event.setCancelled(true);
            return;
        }

        attackTime = System.currentTimeMillis();
    };
    
	@EventLink(value = Priority.VERY_HIGH)
	public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        if (Math.random() > chance.getValue().intValue()) {
            currentShouldAttack = true;
        } else {
            switch (mode.getValue().getName()) {
                case "Legitimate KeepSprint":
                    currentShouldAttack = mc.player.hurtTime > 0 && !mc.player.onGround && MoveUtil.isMoving();
                    break;
                case "KnockBack Reduction":
                    currentShouldAttack = !mc.player.onGround && mc.player.motionY < 0;
                    break;
                case "Critical hit frequency":
                	currentShouldAttack = !mc.player.onGround && mc.player.fallDistance > 0.3;
                	break;
            }

            if (!currentShouldAttack)
                currentShouldAttack = System.currentTimeMillis() - attackTime >= delay.getValue().intValue();
        }
	};
    
    public boolean canAttack(Entity target) {
        return canSwing();
    }

    public boolean canSwing() {
        if (!isEnabled()) return true;
        return currentShouldAttack;
    }

}