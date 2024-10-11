package cc.unknown.ui.menu.account.impl;

import java.awt.Color;
import java.io.IOException;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.ui.menu.account.AccountManagerScreen;
import cc.unknown.ui.menu.component.button.MenuButton;
import cc.unknown.ui.menu.component.button.impl.MenuAccountTypeButton;
import cc.unknown.util.Accessor;
import cc.unknown.util.MouseUtil;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.animation.Easing;
import cc.unknown.util.font.Font;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.sound.SoundUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class AccountScreen extends GuiScreen implements Accessor {
    private static final Font FONT_RENDERER = Fonts.MINECRAFT.get(36, Weight.LIGHT);
    private static final ResourceLocation CRACKED_RESOURCE = new ResourceLocation("sakura/images/minecraft.png");
    private static final ResourceLocation COOKIE_RESOURCE = new ResourceLocation("sakura/images/cookie.png");
    private static final ResourceLocation BACK_RESOURCE = new ResourceLocation("sakura/images/back.png");

    private static GuiScreen reference;

    private final MenuAccountTypeButton[] menuButtons = new MenuAccountTypeButton[3];

    public AccountScreen() {
        reference = this;
    }
    
    @Override
    public void initGui() {
        int buttons = menuButtons.length;
        int buttonWidth = 100;
        int buttonHeight = 140;
        int buttonPadding = 5;

        int buttonX = width / 2 - ((buttons & 1) == 0 ?
                (buttonWidth + buttonPadding) * (int) (buttons / 2) + buttonPadding / 2 :
                (buttonWidth + buttonPadding) * (int) (buttons / 2) + buttonWidth / 2);

        menuButtons[0] = new MenuAccountTypeButton(0, 0, 0, 0, () -> mc.displayGuiScreen(new CrackedScreen()), "Cracked", CRACKED_RESOURCE);
        menuButtons[1] = new MenuAccountTypeButton(0, 0, 0, 0, () -> mc.displayGuiScreen(new CookieScreen()), "Cookie", COOKIE_RESOURCE);
        menuButtons[2] = new MenuAccountTypeButton(0, 0, 0, 0, () -> mc.displayGuiScreen(new AccountManagerScreen(reference)), "Back", BACK_RESOURCE);

        for (MenuButton button : menuButtons) {
            button.setX(buttonX);
            button.setY(height / 2 - buttonHeight / 2 + 24);
            button.setWidth(buttonWidth);
            button.setHeight(buttonHeight);
            buttonX += buttonWidth + buttonPadding;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        BackgroundUtil.renderBackground(this);

        for (MenuButton button : menuButtons) {
            button.draw(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (MenuButton button : menuButtons) {
            if (MouseUtil.isHovered(button.getX(), button.getY(), button.getWidth(), button.getHeight(), mouseX, mouseY)) {
                button.runAction();
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                break;
            }
        }
    }
}
