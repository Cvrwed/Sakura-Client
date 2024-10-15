package cc.unknown.module.impl.visual;

import org.lwjgl.input.Keyboard;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.GuiKeyBoardEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.time.StopWatch;

@ModuleInfo(aliases = "Click GUI", description = "Displays a GUI that allows you to toggle modules and edit their settings", category = Category.VISUALS, keyBind = Keyboard.KEY_RSHIFT)
public final class ClickGUI extends Module {
    private final StopWatch stopWatch = new StopWatch();

    @Override
    public void onEnable() {
    	mc.displayGuiScreen(Sakura.instance.getClickGui());
    	stopWatch.reset();
    }

    @Override
    public void onDisable() {
    	mc.setIngameFocus();
    	Keyboard.enableRepeatEvents(false);
    	Sakura.instance.getEventBus().unregister(Sakura.instance.getClickGui());
    	threadPool.execute(() -> Sakura.instance.getConfigManager().get("latest").write());
    }

    @EventLink(value = Priority.HIGH)
    public final Listener<Render2DEvent> onRender2D = event -> {
        Sakura.instance.getClickGui().render();
    };

    @EventLink
    public final Listener<GuiKeyBoardEvent> onKey = event -> {
        if (!stopWatch.finished(50)) return;

        if (event.getKeyCode() == this.getKey()) {
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    };
}
