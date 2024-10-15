package cc.unknown.util;

import com.google.gson.Gson;

import cc.unknown.Sakura;
import cc.unknown.component.impl.Component;
import cc.unknown.module.Module;
import cc.unknown.ui.clickgui.ClickGui;
import cc.unknown.ui.theme.Themes;
import net.minecraft.client.Minecraft;

public interface Accessor {
    Minecraft mc = Minecraft.getMinecraft();

    default Sakura getInstance() {
        return Sakura.instance;
    }
    
    default boolean isInGame() {
        return mc != null || mc.player != null || mc.world != null;
    }

    default boolean isClickGui() {
    	return mc.gameSettings.showDebugInfo || mc.currentScreen instanceof ClickGui;
    }
    
    default ClickGui getClickGUI() {
        return getInstance().getClickGui();
    }

    default <T extends Component> T getComponent(Class<T> component) {
        return getInstance().getComponentManager().get(component);
    }

    default Themes getTheme() {
        return getInstance().getThemeManager().getTheme();
    }

    default <T extends Module> T getModule(final Class<T> clazz) {
        return getInstance().getModuleManager().get(clazz);
    }

    default Gson getGSON() {
        return getInstance().getGSON();
    }
}
