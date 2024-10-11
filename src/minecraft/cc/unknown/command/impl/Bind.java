package cc.unknown.command.impl;

import org.lwjgl.input.Keyboard;

import cc.unknown.Sakura;
import cc.unknown.bindable.Bindable;
import cc.unknown.command.Command;
import cc.unknown.util.chat.ChatUtil;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public final class Bind extends Command {

    public Bind() {
        super("Binds a module to the given key", "bind", "b");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 3) {
            final Bindable bindable = Sakura.instance.getBindableManager().get(args[1]);

            if (bindable == null) {
            	error("Invalid module");
                return;
            }

            final String inputCharacter = args[2].toUpperCase();
            final int keyCode = Keyboard.getKeyIndex(inputCharacter);

            bindable.setKey(keyCode);
            ChatUtil.display("Binded " + bindable.getName() + " to " + Keyboard.getKeyName(keyCode) + ".");
        } else if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
            Sakura.instance.getBindableManager().getBinds().forEach(module -> {
                if (module.getKey() != 0) {
                    final String color = getTheme().getChatAccentColor().toString();

                    final ChatComponentText chatText = new ChatComponentText(color + "> " + module.getAliases()[0] + "§f " + Keyboard.getKeyName(module.getKey()));
                    final ChatComponentText hoverText = new ChatComponentText("Click to remove " + module.getAliases()[0] + " bind");

                    chatText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ".bind " + module.getName().replace(" ", "") + " none"))
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

                    mc.player.addChatMessage(chatText);
                }
            });

        } else {
        	warning(".bind <list/module/config> (KEY)");
        }
    }
}