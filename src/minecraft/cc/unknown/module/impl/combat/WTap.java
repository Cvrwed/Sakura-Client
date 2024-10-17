package cc.unknown.module.impl.combat;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "WTap", description = "Briefly releases W after attacking to increase knockback given", category = Category.COMBAT)
public class WTap extends Module {
	
	private final BoundsNumberValue hurtResist = new BoundsNumberValue("Hurt Resistant Time", this, 10, 10, 0, 10, 1);
	private final BoundsNumberValue preDelay = new BoundsNumberValue("Pre Delay", this, 25, 55, 1, 500, 1);
	private final BoundsNumberValue delay = new BoundsNumberValue("Delay", this, 25, 55, 1, 500, 1);
	private final BoundsNumberValue onceEvery =  new BoundsNumberValue("Once every ... hits", this, 1, 1, 1, 10, 1);
	private final BoundsNumberValue range = new BoundsNumberValue("Range", this, 3, 3.5, 1, 6, 0.5);
	private final NumberValue chance = new NumberValue("Chance", this, 90, 0, 100, 1);

	private boolean comboing, hitCoolDown, alreadyHit, waitingForPostDelay;
	private int hitTimeout, hitsWaited;
	private StopWatch postDelayTimer = new StopWatch();
	private StopWatch preDelayTimer = new StopWatch();
	private EntityPlayer target;

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
	    if (target == null) return;

	    if (waitingForPostDelay) {
	        if (postDelayTimer.hasFinished()) {
	            resetPostDelay();
	        }
	        return;
	    }

	    if (comboing) {
	        if (preDelayTimer.hasFinished()) {
	            finishCombo();
	        }
	        return;
	    }

	    if (isValidTarget() && isInRange()) {
	        if (canHit(target)) {
	            if (hitCoolDown && !alreadyHit) {
	                handleCooldown();
	            } else if (!alreadyHit) {
	                setupCombo();
	            }
	        } else {
	            alreadyHit = false;
	        }
	    }
	};

	private boolean isValidTarget() {
	    return mc.objectMouseOver != null 
	        && mc.objectMouseOver.entityHit instanceof Entity 
	        && Mouse.isButtonDown(0);
	}

	private boolean isInRange() {
	    Entity target = mc.objectMouseOver.entityHit;
	    return !target.isDead 
	        && mc.player.getDistanceToEntity(target) <= range.getValue().doubleValue() || mc.player.getDistanceToEntity(target) >= range.getSecondValue().doubleValue();
	}

	private boolean canHit(EntityPlayer target) {
	    return (target.hurtResistantTime >= hurtResist.getValue().intValue() || target.hurtResistantTime <= hurtResist.getSecondValue().intValue())
	        && (chance.getValue().intValue() == 100 
	        || Math.random() <= chance.getValue().intValue() / 100);
	}

	private void handleCooldown() {
	    hitsWaited++;
	    if (hitsWaited >= hitTimeout) {
	        hitCoolDown = false;
	        hitsWaited = 0;
	    } else {
	        alreadyHit = true;
	    }
	}

	private void setupCombo() {
	    hitTimeout = calculateHitTimeout();
	    hitCoolDown = true;
	    hitsWaited = 0;

	    preDelayTimer.setMillis(calculateRandomMillis(preDelay));
	    if (delay.getSecondValue().intValue() != 0) {
	        postDelayTimer.setMillis(calculateRandomMillis(delay));
	        postDelayTimer.reset();
	        waitingForPostDelay = true;
	    } else {
	        comboing = true;
	        startCombo();
	        preDelayTimer.reset();
	    }

	    alreadyHit = true;
	}

	private int calculateHitTimeout() {
	    return onceEvery.getValue().intValue() == onceEvery.getSecondValue().intValue() 
	        ? (int) onceEvery.getValue().intValue() 
	        : ThreadLocalRandom.current().nextInt((int) onceEvery.getValue().intValue(),
	                                              (int) onceEvery.getSecondValue().intValue());
	}

	private long calculateRandomMillis(BoundsNumberValue range) {
	    return (long) ThreadLocalRandom.current().nextDouble(range.getValue().intValue(),
	                                                         range.getSecondValue().intValue() + 0.01);
	}

	private void resetPostDelay() {
	    waitingForPostDelay = false;
	    comboing = true;
	    startCombo();
	    preDelayTimer.reset();
	}

	private void finishCombo() {
	    if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
	        mc.gameSettings.keyBindForward.setPressed(false);
	    }
	    comboing = false;
	}

	private void startCombo() {
	    if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
	    	mc.gameSettings.keyBindForward.setPressed(false);
	    	mc.gameSettings.keyBindForward.pressTime++;
	    }
	}
}