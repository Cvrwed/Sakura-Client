package net.minecraft.client.gui;

import net.minecraft.util.IProgressUpdate;
import net.optifine.CustomLoadingScreen;
import net.optifine.CustomLoadingScreens;

public class GuiScreenWorking extends GuiScreen implements IProgressUpdate {
    private String field_146591_a = "";
    private String field_146589_f = "";
    private int progress;
    private boolean doneWorking;
    private final CustomLoadingScreen customLoadingScreen = CustomLoadingScreens.getCustomLoadingScreen();

    /**
     * Shows the 'Saving level' string.
     */
    public void displaySavingString(final String message) {
        this.resetProgressAndMessage(message);
    }

    /**
     * this string, followed by "working..." and then the "% complete" are the 3 lines shown. This resets progress to 0,
     * and the WorkingString to "working...".
     */
    public void resetProgressAndMessage(final String message) {
        this.field_146591_a = message;
        this.displayLoadingString("Working...");
    }

    /**
     * Displays a string on the loading screen supposed to indicate what is being done currently.
     */
    public void displayLoadingString(final String message) {
        this.field_146589_f = message;
        this.setLoadingProgress(0);
    }

    /**
     * Updates the progress bar on the loading screen to the specified amount. Args: loadProgress
     */
    public void setLoadingProgress(final int progress) {
        this.progress = progress;
    }

    public void setDoneWorking() {
        this.doneWorking = true;
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if (this.doneWorking) {
            if (!this.mc.func_181540_al()) {
                this.mc.displayGuiScreen(null);
            }
        } else {
            if (this.customLoadingScreen != null && this.mc.world == null) {
                this.customLoadingScreen.drawBackground(this.width, this.height);
            } else {
                this.drawDefaultBackground();
            }

            if (this.progress > 0) {
                this.drawCenteredString(this.fontRendererObj, this.field_146591_a, this.width / 2, 70, 16777215);
                this.drawCenteredString(this.fontRendererObj, this.field_146589_f + " " + this.progress + "%", this.width / 2, 90, 16777215);
            }

            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
}
