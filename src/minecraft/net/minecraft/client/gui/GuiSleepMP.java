package net.minecraft.client.gui;

import java.io.IOException;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class GuiSleepMP extends GuiChat {
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height - 40, I18n.format("multiplayer.stopSleeping")));
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 1) {
            this.wakeFromSleep();
        } else if (keyCode != 28 && keyCode != 156) {
            super.keyTyped(typedChar, keyCode);
        } else {
            final String s = this.inputField.getText().trim();

            if (!s.isEmpty()) {
                this.mc.player.sendChatMessage(s);
            }

            this.inputField.setText("");
            this.mc.ingameGUI.getChatGUI().resetScroll();
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 1) {
            this.wakeFromSleep();
        } else {
            super.actionPerformed(button);
        }
    }

    private void wakeFromSleep() {
        final NetHandlerPlayClient nethandlerplayclient = this.mc.player.sendQueue;
        nethandlerplayclient.addToSendQueue(new C0BPacketEntityAction(this.mc.player, C0BPacketEntityAction.Action.STOP_SLEEPING));
    }
}
