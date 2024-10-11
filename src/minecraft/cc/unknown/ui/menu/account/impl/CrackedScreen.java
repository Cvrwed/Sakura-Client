package cc.unknown.ui.menu.account.impl;

import java.io.IOException;

import cc.unknown.ui.menu.account.AccountManagerScreen;
import cc.unknown.ui.menu.main.impl.Button;
import cc.unknown.util.Accessor;
import cc.unknown.util.account.Account;
import cc.unknown.util.account.impl.CrackedAccount;
import cc.unknown.util.account.name.UsernameGenerator;
import cc.unknown.util.font.impl.rise.FontRenderer;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.vector.Vector2d;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

public class CrackedScreen extends GuiScreen implements Accessor {
    private static GuiTextField usernameBox;
    private static GuiScreen reference;

    public CrackedScreen() {
        reference = this;
    }
    
    @Override
    public void initGui() {
    	this.buttonList.clear();
        int boxWidth = 200;
        int boxHeight = 24;
        int padding = 4;
        float buttonWidth = (boxWidth - padding * 2) / 3.0F;

        Vector2d position = new Vector2d(this.width / 2 - boxWidth / 2, this.height / 2 - 24);
        usernameBox = new GuiTextField(0, this.fontRendererObj, (int) position.x, (int) position.y, (int) boxWidth, (int) boxHeight);
    	this.buttonList.add(new Button(1, (int) position.x, (int) position.y + boxHeight + padding, (int) boxWidth, (int) boxHeight, "Generate random"));
    	this.buttonList.add(new Button(2, (int) position.x, (int) position.y + (boxHeight + padding) * 2, (int) buttonWidth, (int) boxHeight, "Add"));
    	this.buttonList.add(new Button(3, (int) ((int) position.x + buttonWidth + padding), (int) position.y + (boxHeight + padding) * 2, (int) buttonWidth, (int) boxHeight, "Login"));
    	this.buttonList.add(new Button(4, (int) ((int) position.x + (buttonWidth + padding) * 2), (int) position.y + (boxHeight + padding) * 2, (int) buttonWidth, (int) boxHeight, "Back"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        BackgroundUtil.renderBackground(this);

        usernameBox.drawTextBox();
        GlStateManager.pushMatrix();
        this.buttonList.forEach(button -> button.drawButton(mc, mouseX, mouseY));
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    	usernameBox.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    	usernameBox.textboxKeyTyped(typedChar, keyCode);
        if (typedChar == '\r') {
            this.actionPerformed(this.buttonList.get(3));
        }
    }
    
    @Override
    public void actionPerformed(final GuiButton button) {
        String username = usernameBox.getText();

    	switch (button.id) {
        case 1: 
        	String name = UsernameGenerator.generate();
        	if (name != null && UsernameGenerator.validate(name)) {
        		usernameBox.setText(name);
        	}
        	break;
        case 2:
            if (UsernameGenerator.validate(username)) {
                Account account = new CrackedAccount(username);
                AccountManagerScreen.addAccount(account);
                account.login();
                mc.displayGuiScreen(new AccountManagerScreen(reference));
            }
        	break;
        case 3:
            if (UsernameGenerator.validate(username)) {
                new CrackedAccount(username).login();
                mc.displayGuiScreen(new AccountManagerScreen(reference));
            }
        	break;
        case 4:
        	mc.displayGuiScreen(new AccountScreen());
        	break;
        }
    }
}
