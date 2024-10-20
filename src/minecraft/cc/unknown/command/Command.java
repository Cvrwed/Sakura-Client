package cc.unknown.command;

import cc.unknown.util.Accessor;
import cc.unknown.util.chat.ChatUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ChatFormatting;

@Getter
public abstract class Command implements Accessor {

    private final String description;
    private final String[] expressions;
    
    public Command(final String description, final String... expressions) {
        this.description = description;
        this.expressions = expressions;
    }

    public abstract void execute(String[] args);
	
	public void error(String errr) {
		ChatUtil.display(ChatFormatting.YELLOW + "[" + ChatFormatting.RED + "%" + ChatFormatting.YELLOW + "] " + ChatFormatting.RESET + errr); 
	}
	
	public void warning(String warn) {
		ChatUtil.display(ChatFormatting.YELLOW + "[" + ChatFormatting.RED + "!" + ChatFormatting.YELLOW + "] " + ChatFormatting.RESET + warn);
	}
	
	public void success(String success) {
		ChatUtil.display(ChatFormatting.YELLOW + "[" + ChatFormatting.GREEN + "*" + ChatFormatting.YELLOW + "] " + ChatFormatting.RESET + success);
	}
}