package cc.unknown.ui.menu.account.impl;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.ui.menu.account.AccountManagerScreen;
import cc.unknown.ui.menu.account.display.AccountViewModel;
import cc.unknown.ui.menu.component.button.MenuButton;
import cc.unknown.ui.menu.main.impl.Button;
import cc.unknown.util.Accessor;
import cc.unknown.util.MouseUtil;
import cc.unknown.util.account.impl.MicrosoftAccount;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.animation.Easing;
import cc.unknown.util.font.Font;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.shader.RiseShaders;
import cc.unknown.util.vector.Vector2d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class MicrosoftScreen extends GuiScreen implements Accessor {
    private static final Font FONT_RENDERER = Fonts.MAIN.get(36, Weight.BOLD);
    private static final Font INFO_FONT_RENDERER = Fonts.MAIN.get(18, Weight.LIGHT);
    private static AccountViewModel<MicrosoftAccount> accountViewModel;
    private static GuiScreen reference;
    private Animation animation;

    public MicrosoftScreen() {
        reference = this;
    }
    
    @Override
    public void initGui() {
    	buttonList.clear();
        int boxWidth = 200;
        int boxHeight = 24;
        int padding = 4;
        float buttonWidth = (boxWidth - padding) / 2.0F;

        Vector2d position = new Vector2d(width / 2 - boxWidth / 2, height / 2 + 76);
        this.buttonList.add(new Button(1, (int) position.x, (int) position.y + boxHeight + padding, (int) boxWidth, (int) boxHeight, "Add"));
        this.buttonList.add(new Button(2, (int) (position.x + buttonWidth + padding), (int) position.y, (int) buttonWidth, (int) boxHeight, "Back"));
        accountViewModel = new AccountViewModel<>(MicrosoftAccount.create(), width / 2 - 100, height / 2 + 32, 200, 40);
        accountViewModel.setScreenHeight(height);

        animation = new Animation(Easing.EASE_OUT_QUINT, 600);
        animation.setStartValue(-200);
    }
    
    @Override
    public void actionPerformed(final GuiButton button) {
    	switch (button.id) {
    	case 1:
            if (accountViewModel.getAccount().isValid()) {
                AccountManagerScreen.addAccount(accountViewModel.getAccount());
                mc.displayGuiScreen(new AccountManagerScreen(reference));
            }
    		break;
    	case 2:
    		mc.displayGuiScreen(new AccountManagerScreen(reference));
    		break;
    	}
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Renders the background.
        if (accountViewModel.getAccount().isValid()) {
            animation.run(36);
        } else {
            animation.run(0);
        }

        BackgroundUtil.renderBackground(this);

        int lineHeight = 20;
        int backgroundHeight = (lineHeight * 4) + 40;
        int backgroundWidth = 300;
        int boxX = width / 2 - (backgroundWidth / 2);
        int boxY = (int) (height / 2 - 96 + animation.getValue());
        int boxHeightTotal = (int) (backgroundHeight + animation.getValue());

        RenderUtil.drawRoundedRect2(boxX, boxY, boxX + backgroundWidth, boxY + boxHeightTotal, 6.0, new Color(0, 0, 0, 150).getRGB());

        FONT_RENDERER.drawCentered("Log in to your microsoft account", width / 2, boxY + 10, Color.WHITE.getRGB());
        INFO_FONT_RENDERER.drawCentered("A link has been copied to your clipboard.", width / 2, boxY + 40, Color.WHITE.darker().getRGB());
        INFO_FONT_RENDERER.drawCentered("To login to your own account, just fill out the form.", width / 2, boxY + 70, Color.WHITE.darker().getRGB());

        int circleX = width / 2;
        int circleY = height / 2 + 4;
        int radius = 12;
        
        if (!accountViewModel.getAccount().isValid()) {
        	GlStateManager.pushMatrix();
        	GlStateManager.translate(circleX, circleY + animation.getValue(), 0);
        	
        	GlStateManager.pushMatrix();
        	GlStateManager.enableBlend();
        	GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        	GlStateManager.disableAlpha();
        	GlStateManager.disableTexture2D();
        	
        	GL11.glEnable(GL11.GL_POINT_SMOOTH);
        	GL11.glPointSize(8);
        	GL11.glBegin(GL11.GL_POINTS);
        	long offset = (long) (Minecraft.getSystemTime() * 0.5);
        	offset %= 360;
        	offset -= offset % 30;
        	
        	for (int i = 0; i < 360; i += 30) {
        		double angle = Math.PI * 2 * i / 360;
        		double cos = Math.cos(angle);
        		double sin = Math.sin(angle);
        		float alpha = 1.0F - ((float) (i + offset) % 360) / 360.0F;
        		GL11.glColor4f(1, 1, 1, alpha);
        		GL11.glVertex2d(cos * radius, sin * radius);
        	}
        	
        	GL11.glEnd();
        	GL11.glDisable(GL11.GL_POINT_SMOOTH);
        	GL11.glLineWidth(1);
        	
        	GlStateManager.enableTexture2D();
        	GlStateManager.disableBlend();
        	GlStateManager.enableAlpha();
        	GlStateManager.popMatrix();
        	GlStateManager.popMatrix();
        }

        accountViewModel.draw();
    }
}
