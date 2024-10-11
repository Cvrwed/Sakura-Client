package cc.unknown.module.impl.other;

import java.util.Arrays;

import cc.unknown.component.impl.render.NotificationComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(aliases = "Auto Play", description = "Joins a new game after a win", category = Category.OTHER)
public final class AutoPlay extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Hypixel"))
			.add(new SubMode("Universocraft"))
			.setDefault("Universocraft");
	
	private final ModeValue uniMods = new ModeValue("Universocraft Mode", this, () -> !mode.is("Universocraft"))
			.add(new SubMode("Skywars"))
			.add(new SubMode("Bedwars"))
			.add(new SubMode("Tnt Tag"))
			.setDefault("Skywars");
	
	private final ModeValue hypMode = new ModeValue("Hypixel Mode", this, () -> !mode.is("Hypixel"))
			.add(new SubMode("Solo Insane"))
			.add(new SubMode("Solo Normal"))
			.setDefault("Solo Insane");
	
	@EventLink
	public final Listener<PacketEvent> onPacket = event -> {
	    Packet<?> packet = event.getPacket();
	    if (!event.isReceive()) return;
	    if (packet instanceof S02PacketChat) {
	        S02PacketChat wrapper = ((S02PacketChat) packet);
	        String receiveMessage = wrapper.getChatComponent().getFormattedText();

	        if (containsAny(receiveMessage, "Jugar de nuevo", "ha ganado", "play again?")) {
	            String command = getCommandForMode();
	            
	            if (!command.isEmpty()) {
	                ChatUtil.send(command);
	                NotificationComponent.post("Auto Play", "Joined a new game", 7000);
	            }
	        }
	    }
	};

	private String getCommandForMode() {
	    String modeName = mode.getValue().getName().toLowerCase();
	    
	    switch (modeName) {
	        case "universocraft":
	            return getUniCommand(uniMods.getValue().getName().toLowerCase());
	            
	        case "hypixel":
	            return getHypCommand(hypMode.getValue().getName().toLowerCase());
	            
	        default:
	            return "";
	    }
	}

	private String getUniCommand(String uniMode) {
	    switch (uniMode) {
	        case "skywars":
	            return "/skywars random";
	        case "bedwars":
	            return "/bedwars random";
	        case "tnt tag":
	            return "/playagain";
	        default:
	            return "";
	    }
	}

	private String getHypCommand(String hypMode) {
	    switch (hypMode) {
	        case "solo insane":
	            return "/play solo_insane";
	        case "solo normal":
	            return "/play solo_normal";
	        default:
	            return "";
	    }
	}
	
	private boolean containsAny(String source, String... targets) {
	    return Arrays.stream(targets).anyMatch(source::contains);
	}
}
