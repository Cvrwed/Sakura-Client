package cc.unknown.module.impl.other;

import java.util.Arrays;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.value.impl.StringValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(aliases = "Auto Leave", description = "Get out of the game", category = Category.OTHER)
public final class AutoLeave extends Module {
	
	private final StringValue text = new StringValue("Command", this, "/leave");
	
	@EventLink
	public final Listener<PacketEvent> onPacket = event -> {
	    Packet<?> packet = event.getPacket();
	    if (!event.isReceive()) return;
	    if (packet instanceof S02PacketChat) {
	        S02PacketChat wrapper = ((S02PacketChat) packet);
	        String receiveMessage = wrapper.getChatComponent().getFormattedText();

	        if (containsAny(receiveMessage, "has ganado", "has perdido", "Deseas salirte", "Han ganado")) {
	            String command = text.getValue();
	            
	            if (!command.isEmpty()) {
	                ChatUtil.send(command);
	            }
	        }
	    }
	};

	private boolean containsAny(String source, String... targets) {
	    return Arrays.stream(targets).anyMatch(source::contains);
	}
}
