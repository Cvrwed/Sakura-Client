package cc.unknown.ui.menu.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.ui.menu.account.AccountManagerScreen;
import cc.unknown.ui.menu.main.impl.Button;
import cc.unknown.util.font.Font;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class MainMenu extends GuiMainMenu {

    private Font fontRenderer;
    private final Map<Integer, Consumer<GuiButton>> buttonActions = new HashMap<>();

    public MainMenu() {
        fontRenderer = Fonts.MAIN.get(18, Weight.LIGHT);
        buttonActions.put(0, button -> mc.displayGuiScreen(new GuiSelectWorld(this)));
        buttonActions.put(1, button -> mc.displayGuiScreen(new GuiMultiplayer(this)));
        buttonActions.put(2, button -> mc.displayGuiScreen(new AccountManagerScreen(this)));
        buttonActions.put(3, button -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings)));
        buttonActions.put(4, button -> mc.shutdown());
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        final String[] keys = {"SinglePlayer", "MultiPlayer", "Alt Manager", "Settings", "Exit"};
        final String[] translatedStrings = new String[keys.length];

        for (int i = 0; i < keys.length; i++) {
            translatedStrings[i] = I18n.format(keys[i]);
        }

        final int initHeight = height / 4 + 36;
        final int objHeight = 20;
        final int objWidth = 100;
        final int buttonSpacing = 22;

        final int xMid = width / 2 - objWidth / 2;

        for (int i = 0; i < translatedStrings.length; i++) {
            int offset = i * buttonSpacing;
            this.buttonList.add(new Button(i, xMid, initHeight + offset, objWidth, objHeight, translatedStrings[i]));
        }
    }

    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        buttonActions.getOrDefault(button.id, b -> {}).accept(button);
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        final String title = "§7Sakura Client";
        fontRenderer.drawWithShadow(title, 2.0f, height - 10, -1);

        GlStateManager.pushMatrix();
        this.buttonList.forEach(button -> button.drawButton(mc, mouseX, mouseY));
        GlStateManager.popMatrix();
    }
}