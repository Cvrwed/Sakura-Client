package cc.unknown.module.api;

import cc.unknown.ui.clickgui.screen.Screen;
import cc.unknown.ui.clickgui.screen.impl.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    HOME("Home", new HomeScreen()),
    COMBAT("Combat", new CategoryScreen()),
    PLAYER("Player", new CategoryScreen()),
    MOVEMENT("Move", new CategoryScreen()),
    OTHER("Other", new CategoryScreen()),
    GHOST("Ghost", new CategoryScreen()),
    VISUALS("Visuals", new CategoryScreen()),
    EXPLOIT("Exploit", new CategoryScreen()),
    WORLD("World", new CategoryScreen()),
    THEME("Themes", new ThemeScreen()),
    CONFIG("Configs", new ConfigsScreen()),
    SCRIPT("Scripts", new ScriptScreen());
	
    private final String name;
    public final Screen clickGUIScreen;
}