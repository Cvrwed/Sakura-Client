package cc.unknown;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.lwjgl.opengl.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cc.unknown.bindable.BindableManager;
import cc.unknown.bots.BotManager;
import cc.unknown.command.CommandManager;
import cc.unknown.component.ComponentManager;
import cc.unknown.event.Event;
import cc.unknown.event.bus.impl.EventBus;
import cc.unknown.module.api.manager.ModuleManager;
import cc.unknown.script.ScriptManager;
import cc.unknown.ui.clickgui.ClickGui;
import cc.unknown.ui.theme.ThemeManager;
import cc.unknown.util.creative.SakuraTab;
import cc.unknown.util.file.FileManager;
import cc.unknown.util.file.alt.AltManager;
import cc.unknown.util.file.config.ConfigManager;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;

@Getter
public enum Sakura {
    instance;

    public static final String NAME = "Sakura";
    public static final String VERSION_FULL = "4.8";
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);

    private EventBus<Event> eventBus;
    private ModuleManager moduleManager;
    private ComponentManager componentManager;
    private CommandManager commandManager;
    private BotManager botManager;
    private ThemeManager themeManager;

    private FileManager fileManager;

    private ConfigManager configManager;
    private AltManager altManager;
    private BindableManager bindableManager;
    private ScriptManager scriptManager;

    private ClickGui clickGui;
    private SakuraTab creativeTab;
            
    private Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void init() {
        getOptimization(Minecraft.getMinecraft());

        moduleManager = new ModuleManager();
        componentManager = new ComponentManager();
        commandManager = new CommandManager();
        fileManager = new FileManager();
        configManager = new ConfigManager();
        altManager = new AltManager();
        botManager = new BotManager();
        themeManager = new ThemeManager();
        eventBus = new EventBus<>();
        bindableManager = new BindableManager();
        scriptManager = new ScriptManager();

        fileManager.init();

        moduleManager.init();
        scriptManager.init();
        botManager.init();
        componentManager.init();
        commandManager.init();
        altManager.init();

        clickGui = new ClickGui();
        clickGui.initGui();
        
        creativeTab = new SakuraTab();

        configManager.init();
        bindableManager.init();

        Display.setTitle(NAME + " " + VERSION_FULL);
    }

    public void terminate() {
        if (getConfigManager().get("latest") != null) {
        	getConfigManager().get("latest").write();
        }
        
        System.gc();
    }
    
    private void getOptimization(Minecraft mc) {
    	mc.gameSettings.ofFastRender = true;
        mc.gameSettings.ofChunkUpdatesDynamic = true;
    	mc.gameSettings.ofSmartAnimations = true;
        mc.gameSettings.ofShowGlErrors = false;
        mc.gameSettings.ofRenderRegions = true;
    	mc.gameSettings.ofSmoothFps = false;
        mc.gameSettings.ofFastMath = true;
        mc.gameSettings.useVbo = true;
        mc.gameSettings.guiScale = 2;
    }
}