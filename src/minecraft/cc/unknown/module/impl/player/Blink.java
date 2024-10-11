package cc.unknown.module.impl.player;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.PingSpoofComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.math.MathUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;

/**
 * @author Alan
 * @since 23/10/2021
 */

@ModuleInfo(aliases = {"Blink"}, description = "Temporarily chokes data being sent to the server", category = Category.PLAYER)
public class Blink extends Module {

    public BooleanValue pulse = new BooleanValue("Pulse", this, false);
    public BoundsNumberValue delay = new BoundsNumberValue("Delay", this, 2, 2, 2, 40, 1, () -> !pulse.getValue());
    public int next;
    private EntityOtherPlayerMP blinkEntity;

    @Override
    public void onEnable() {
        getNext();
    }

    @Override
    public void onDisable() {
        deSpawnEntity();
    }
    
    @EventLink
    public final Listener<MotionEvent> onMotion = event -> {
    	if (event.isPre()) {
    		PingSpoofComponent.blink();
    	}
    	
    	if (event.isPost()) {
            if (mc.player.ticksExisted > next && pulse.getValue()) {
                getNext();
                PingSpoofComponent.dispatch();

                deSpawnEntity();
            }
    	}
    };

    @EventLink
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        getNext();
    };

    public void getNext() {
        if (mc.player == null) return;
        next = mc.player.ticksExisted + (int) MathUtil.getRandom(delay.getValue().intValue(), delay.getSecondValue().intValue());
    }

    public void deSpawnEntity() {
        if (blinkEntity != null) {
            Sakura.instance.getBotManager().remove(this, blinkEntity);
            mc.world.removeEntityFromWorld(blinkEntity.getEntityId());
            blinkEntity = null;
        }
    }
}