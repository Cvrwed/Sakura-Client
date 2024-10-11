package net.minecraft.client.gui;

import java.io.IOException;
import java.util.Random;

import cc.unknown.ui.menu.account.AccountManagerScreen;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.BackgroundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback, Accessor {
    private static final Random RANDOM = new Random();
    private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");
    private ResourceLocation backgroundTexture;
    private GuiButton modButton;

    public GuiMainMenu() {

    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        final int i = 24;
        final int j = this.height / 4 + 48;
        
        this.addSingleplayerMultiplayerButtons(j, 24);

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit")));

        this.mc.func_181537_a(false);
    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
     */
    private void addSingleplayerMultiplayerButtons(final int p_73969_1_, final int p_73969_2_) {
    	ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        this.buttonList.add(new GuiButton(1, sr.getScaledWidth() / 2 - 350, sr.getScaledHeight() / 2 + 50 * 2 - 100, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer")));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, "Alts"));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }
        
    	if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        
        if (button.id == 3) {
        	this.mc.displayGuiScreen(new AccountManagerScreen(this));
        }
        
        if (button.id == 4) {
            this.mc.shutdown();
        }
    }
 
    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        GlStateManager.disableAlpha();
        BackgroundUtil.renderBackground(this);
        GlStateManager.enableAlpha();
        final int i = 274;
        final int j = this.width / 2 - i / 2;
        final int k = 30;
        int l = -2130706433;
        int i1 = 16777215;
        int j1 = 0;
        int k1 = Integer.MIN_VALUE;

        this.mc.getTextureManager().bindTexture(minecraftTitleTextures);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {

    }
}
