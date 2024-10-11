package cc.unknown.command.impl;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.module.Module;
import cc.unknown.util.chat.ChatUtil;
import net.minecraft.util.ChatFormatting;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public final class Toggle extends Command {

    public Toggle() {
        super("Toggles the specified module", "toggle", "t");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length != 2) {
            error(String.format(".%s <module>", args[0]));
            return;
        }
        final Module module = Sakura.instance.getModuleManager().get(args[1]);
        if (module == null) {
        	warning("Invalid module");
            return;
        }
        module.toggle();
        ChatUtil.display("Toggled %s",
                module.getAliases()[0] + " " + (module.isEnabled() ? ChatFormatting.GREEN + "on" : ChatFormatting.RED + "off")
        );
    }
}