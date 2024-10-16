package cc.unknown.module.impl.combat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Auto Clicker", description = "Clicks automatically", category = Category.COMBAT)
public class AutoClicker extends Module {

	private final ModeValue button = new ModeValue("Click Button", this) {{
		add(new SubMode("Left"));
		add(new SubMode("Right"));
		add(new SubMode("Both"));
		setDefault("Left");
	}};
	
	private final ModeValue clickMode = new ModeValue("Randomization", this)
			.add(new SubMode("Normal"))
			.add(new SubMode("Butter Fly"))
			.setDefault("Normal");
	
    private final BoundsNumberValue cps = new BoundsNumberValue("CPS", this, 8, 14, 1, 20, 0.1);
    
    private final BooleanValue breakBlocks = new BooleanValue("Break Blocks", this, true, () -> !isButtonClick());
    private final BooleanValue guiClicker = new BooleanValue("Gui Clicker", this, false, () -> !isButtonClick());
    private final NumberValue clickDuration = new NumberValue("Click Duration", this, 2, 0, 5, 1, () -> !isButtonClick() || !guiClicker.getValue());
    private final NumberValue randomizationFactor = new NumberValue("Click Randomization Factor", this, 0.1, 0.1, 1, 0.1, () -> !isButtonClick() || !guiClicker.getValue());
    
    private final StopWatch stopWatch = new StopWatch();
    private int ticksDown;
    private int attackTicks;
    private int mouseDownTicks = 0;
    private long nextSwing;

    @EventLink
    public final Listener<TickEvent> onTick = event -> {
    	attackTicks++;
    	HitSelect hitSelect = Sakura.instance.getModuleManager().get(HitSelect.class);

        if (hitSelect != null && stopWatch.finished(nextSwing) && (!hitSelect.isEnabled() || ((hitSelect.isEnabled() && attackTicks >= 10) || (mc.player != null && mc.player.hurtTime > 0 && stopWatch.finished(nextSwing)))) && mc.currentScreen == null) {
            final long clicks = (long) (this.cps.getRandomBetween().longValue() * 1.5);

            if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                ticksDown++;
            } else {
                ticksDown = 0;
            }
            
            switch (clickMode.getValue().getName()) {
            case "Normal":
            	this.nextSwing = 1000 / clicks;
            	break;
            case "Butter Fly":
            	if (this.nextSwing >= 100) {
                    this.nextSwing = (long) (Math.random() * 100);
                }
            	break;
            }
            
            switch (button.getValue().getName()) {
            case "Left":
            	handleLeftClick();
            	break;
            case "Right":
            	handleRightClick();
            	break;
            case "Both":
            	handleLeftClick();
            	handleRightClick();
            	break;
            }

            this.stopWatch.reset();
        }
    };
    
    @EventLink
    public final Listener<PreMotionEvent> onMotion = event -> {
        if (guiClicker.getValue()) {
        	inInvClick(mc.currentScreen);
        }
    };
    
    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
        attackTicks = 0;
    };
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        mc.leftClickCounter = 0;
    };
    
    private void handleLeftClick() {
        if (ticksDown > 1 && !mc.gameSettings.keyBindUseItem.isKeyDown() && (!breakBlocks.getValue() || mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)) {
            PlayerUtil.sendClick(0, true);
        } else if (!breakBlocks.getValue()) {
            mc.playerController.curBlockDamageMP = 0;
        }
    }
    
    private void handleRightClick() {
        if (mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.gameSettings.keyBindAttack.isKeyDown()) {
            PlayerUtil.sendClick(1, true);

            if (Math.random() > 0.9) {
                PlayerUtil.sendClick(1, true);
            }
        }
    }
    
    private void inInvClick(GuiScreen gui) {
        if (gui == null) return;

        int mouseX = Mouse.getX() * gui.width / mc.displayWidth;
        int mouseY = gui.height - Mouse.getY() * gui.height / mc.displayHeight - 1;

        try {
            Method guiClicker = GuiScreen.class.getDeclaredMethod("mouseClicked", Integer.TYPE, Integer.TYPE, Integer.TYPE);
            guiClicker.setAccessible(true);

            mouseDownTicks++;
            if (mouseDownTicks > clickDuration.getValue().intValue() && Math.random() > randomizationFactor.getValue().intValue()) {
            	guiClicker.invoke(gui, mouseX, mouseY, 0);
            	mouseDownTicks = 0;
            } else {
                mouseDownTicks = 0;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    private boolean isButtonClick() {
    	return button.is("Left") || button.is("Both");
    }
}