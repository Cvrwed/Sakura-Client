package cc.unknown.module.impl.ghost;

import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.math.MathUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Auto Clicker", description = "Clicks automatically", category = Category.GHOST)
public class AutoClicker extends Module {

    private final BoundsNumberValue cps = new BoundsNumberValue("CPS", this, 8, 14, 1, 40, 0.1);
    private final BooleanValue rightClick = new BooleanValue("Right Click", this, false);
    private final BooleanValue leftClick = new BooleanValue("Left Click", this, true);
    private final BooleanValue breakBlocks = new BooleanValue("Break Blocks", this, true);
    private final BooleanValue invClicker = new BooleanValue("Works on Gui", this, true);

    private final StopWatch stopWatch = new StopWatch();
    private int ticksDown, attackTicks, mouseDownTicks;
    private long nextSwing;
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        mc.leftClickCounter = 0;
    };
    
    @EventLink
    public final Listener<MotionEvent> onMotion = event -> {
    	if (!event.isPre()) return;
    	
		if (mc.currentScreen instanceof GuiContainer && invClicker.getValue()) {
			GuiContainer container = ((GuiContainer) mc.currentScreen);

			final int mouseX = Mouse.getEventX() * container.width / this.mc.displayWidth;
			final int mouseY = container.height - Mouse.getEventY() * container.height / this.mc.displayHeight - 1;

			try {
				if (mc.gameSettings.keyBindAttack.isKeyDown()) {
					mouseDownTicks++;
					if (mouseDownTicks > 2 && Math.random() > 0.1)
						container.mouseClicked(mouseX, mouseY, 0);
				} else if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
					mouseDownTicks++;
					if (mouseDownTicks > 2 && Math.random() > 0.1)
						container.mouseClicked(mouseX, mouseY, 1);
				} else {
					mouseDownTicks = 0;
				}

			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
    };
    
    @EventLink
    public final Listener<TickEvent> onTick = event -> {
    	attackTicks++;
    	stopWatch.setMillis(nextSwing);
        
        HitSelect hitSelect = Sakura.instance.getModuleManager().get(HitSelect.class);
            
        if (hitSelect != null && stopWatch.finished(nextSwing) && (!hitSelect.isEnabled() || ((hitSelect.isEnabled() && attackTicks >= 10) || (mc.player != null && mc.player.hurtTime > 0 && stopWatch.finished(nextSwing)))) && mc.currentScreen == null) {
        	final long clicks = (long) (MathUtil.randomizeDouble(cps.getValue().longValue(), cps.getSecondValue().longValue()));

        	if (mc.gameSettings != null && mc.gameSettings.keyBindAttack != null) {
        		if (mc.gameSettings.keyBindAttack.isKeyDown()) {
        			ticksDown++;
        		} else {
        			ticksDown = 0;
        		}
                        
        		nextSwing = 1000 / clicks;
        		
        		if (rightClick != null && rightClick.getValue() && mc.gameSettings.keyBindUseItem != null && mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.gameSettings.keyBindAttack.isKeyDown()) {
        			PlayerUtil.sendClick(1, true);
                            
        			if (Math.random() > 0.9) {
        				PlayerUtil.sendClick(1, true);
        			}
        		}

        		if (leftClick.getValue() && ticksDown > 1 && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)) {
        			PlayerUtil.sendClick(0, true);
        		} else if (breakBlocks != null && !breakBlocks.getValue()) {
        			mc.playerController.curBlockDamageMP = 0;
        		}
        		
        		stopWatch.reset();
        	}
        }
    };
}