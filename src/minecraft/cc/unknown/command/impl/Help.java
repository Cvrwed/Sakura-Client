package cc.unknown.command.impl;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.util.chat.ChatUtil;
import net.minecraft.util.ChatFormatting;

/**
 * @author Auth
 * @since 3/02/2022
 */
public final class Help extends Command {

    public Help() {
        super("Gives you a list of all commands", "help");
    }
    
    @Override
    public void execute(final String[] args) {
    	String prefix = ChatFormatting.YELLOW + "[" + ChatFormatting.RED + "*" + ChatFormatting.YELLOW + "]" + ChatFormatting.RESET + " ";
        Sakura.instance.getCommandManager().getCommandList()
                .forEach(command -> ChatUtil.display(prefix + StringUtils.capitalize(command.getExpressions()[0]) + " " + Arrays.toString(command.getExpressions()) + ": " + ChatFormatting.GRAY + command.getDescription()));
    }
}