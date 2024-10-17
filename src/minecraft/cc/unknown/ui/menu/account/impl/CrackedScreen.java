package cc.unknown.ui.menu.account.impl;

import java.awt.Color;
import java.io.IOException;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.ui.menu.account.AccountManagerScreen;
import cc.unknown.ui.menu.main.impl.Button;
import cc.unknown.ui.menu.main.impl.TextField;
import cc.unknown.util.Accessor;
import cc.unknown.util.account.Account;
import cc.unknown.util.account.impl.CrackedAccount;
import cc.unknown.util.account.name.UsernameGenerator;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.animation.Easing;
import cc.unknown.util.font.Font;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.vector.Vector2d;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

public class CrackedScreen extends GuiScreen implements Accessor {
    private static TextField usernameBox;
    private static GuiScreen reference;
    private String userDisplay = "Idle...";
    private Animation animation;
    private static final Font FONT_RENDERER = Fonts.MAIN.get(20, Weight.LIGHT);

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
        usernameBox = new TextField(0, this.fontRendererObj, (int) position.x, (int) position.y, (int) boxWidth, (int) boxHeight);
    	this.buttonList.add(new Button(1, (int) position.x, (int) position.y + boxHeight + padding, (int) boxWidth, (int) boxHeight, "Generate random"));
    	this.buttonList.add(new Button(2, (int) position.x, (int) position.y + (boxHeight + padding) * 2, (int) buttonWidth, (int) boxHeight, "Add"));
    	this.buttonList.add(new Button(3, (int) ((int) position.x + buttonWidth + padding), (int) position.y + (boxHeight + padding) * 2, (int) buttonWidth, (int) boxHeight, "Login"));
    	this.buttonList.add(new Button(4, (int) ((int) position.x + (buttonWidth + padding) * 2), (int) position.y + (boxHeight + padding) * 2, (int) buttonWidth, (int) boxHeight, "Back"));
        animation = new Animation(Easing.EASE_OUT_QUINT, 600);
        animation.setStartValue(-200);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        BackgroundUtil.renderBackground(this);
        animation.run(0);

        usernameBox.drawTextBox();
        GlStateManager.pushMatrix();
        int backgroundWidth = FONT_RENDERER.width(userDisplay) + 10;
        int backgroundHeight = (int) (FONT_RENDERER.height() + 5);

        int backgroundX = (width / 2) - (backgroundWidth / 2);
        int backgroundY = (int) ((height / 2 - 55 + animation.getValue()) - (backgroundHeight / 2));

        RenderUtil.drawRoundedRect2(backgroundX, backgroundY + animation.getValue(), backgroundX + backgroundWidth, backgroundY + backgroundHeight + animation.getValue(), 6.0, new Color(0, 0, 0, 150).getRGB());
        FONT_RENDERER.drawCentered(userDisplay, width / 2, height / 2 - 58 + animation.getValue(), Color.WHITE.getRGB());

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
        	userDisplay = "Like the name " + name + "?";
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
