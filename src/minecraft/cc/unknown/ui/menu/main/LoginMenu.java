package cc.unknown.ui.menu.main;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class LoginMenu extends GuiScreen {
	
	private static GuiTextField userName;
	
    @Override
    public void initGui() {
    	mc.displayGuiScreen(new MainMenu());
    }
}
