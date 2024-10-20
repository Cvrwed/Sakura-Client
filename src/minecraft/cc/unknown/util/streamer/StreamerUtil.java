package cc.unknown.util.streamer;

import com.mojang.realmsclient.gui.ChatFormatting;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
@Getter
public class StreamerUtil {
	public ChatFormatting yellow = ChatFormatting.YELLOW;
	public ChatFormatting red = ChatFormatting.RED;
	public ChatFormatting reset = ChatFormatting.RESET;
	public ChatFormatting aqua = ChatFormatting.AQUA;
	public ChatFormatting gray = ChatFormatting.GRAY;
	public ChatFormatting green = ChatFormatting.GREEN;
	public ChatFormatting blue = ChatFormatting.BLUE;
	public ChatFormatting black = ChatFormatting.BLACK;
	public ChatFormatting gold = ChatFormatting.GOLD;
	
	public ChatFormatting darkAqua = ChatFormatting.DARK_AQUA;
	public ChatFormatting darkGray = ChatFormatting.DARK_GRAY;
	public ChatFormatting darkPurple = ChatFormatting.DARK_PURPLE;
	public ChatFormatting darkBlue = ChatFormatting.DARK_BLUE;
	public ChatFormatting darkGreen = ChatFormatting.DARK_GREEN;
	public ChatFormatting darkRed = ChatFormatting.DARK_RED;

	public ChatFormatting lightPurple = ChatFormatting.LIGHT_PURPLE;

	public String getPrefix(String rank, ChatFormatting rankColor) {
		return darkGray + "[" + rankColor + rank + darkGray + "] " + rankColor;
	}
}
