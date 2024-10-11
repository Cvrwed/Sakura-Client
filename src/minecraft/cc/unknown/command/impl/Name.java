package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public final class Name extends Command {

    public Name() {
        super("Copies and displays your username", "name", "ign", "username", "nick", "nickname");
    }

    @Override
    public void execute(final String[] args) {
        final String name = PlayerUtil.name();

        GuiScreen.setClipboardString(name);
        ChatUtil.display("Copied your username to clipboard. (%s)", name);
    }
}
