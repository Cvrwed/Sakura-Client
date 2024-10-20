package cc.unknown.module.impl.visual;

import static cc.unknown.util.streamer.StreamerUtil.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.mojang.realmsclient.gui.ChatFormatting;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.FriendAndTargetComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.RenderTextEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.StringValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "Streamer", description = "Hides your name", category = Category.VISUALS)
public final class Streamer extends Module {

	private final ModeValue mode = new ModeValue("Mode", this)
			.add(new SubMode("Universocraft"))
			.add(new SubMode("Normal"))
			.setDefault("Universocraft");
	
	private final BooleanValue rankSpoof = new BooleanValue("Rank Spoof", this, false, () -> !mode.is("Universocraft"));
	private final BooleanValue cosmeticSpoof = new BooleanValue("Cosmetic Spoof", this, false, () -> !mode.is("Universocraft") || !rankSpoof.getValue());
	private final StringValue tagSpoof = new StringValue("Tag", this, "MTF", () -> !mode.is("Universocraft") || !cosmeticSpoof.getValue() || !rankSpoof.getValue());
	private final ModeValue spoofRank = new ModeValue("Rank", this, () -> !mode.is("Universocraft") || !rankSpoof.getValue())
			.add(new SubMode("Jup"))
			.add(new SubMode("Nep"))
			.add(new SubMode("Mer"))
			.add(new SubMode("Sat"))
			.add(new SubMode("Ayu"))
			.add(new SubMode("Bui"))
			.add(new SubMode("Mod"))
			.add(new SubMode("Adm"))
			.setDefault("Jup");

    public final StringValue replacement = new StringValue("Spoof Name: ", this, "You");
    private final BooleanValue checkFriends = new BooleanValue("Check Friends", this, false);
    private final StringValue protectFriends = new StringValue("Protect Friends: ", this, "Friend", () -> !checkFriends.getValue());

    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<RenderTextEvent> onRenderText = event -> {
        assert mc.player != null;
        String text = event.getString();
        String playerName = mc.player.getName();
        String mode = spoofRank.getValue().getName();
        String newName = replacement.getValue();
        
        if (text.startsWith("/") || text.startsWith(Sakura.instance.getCommandManager().getPrefix())) {
            return;
        }

        if (text.contains(playerName)) {
            text = text.replaceAll("\\[[A-Za-z]{3}\\]\\s*", "");

            if (rankSpoof.getValue()) {
                Map<String, ChatFormatting> ranks = new HashMap<>();
                ranks.put("Jup", aqua);
                ranks.put("Nep", blue);
                ranks.put("Mer", darkGreen);
                ranks.put("Sat", darkPurple);
                ranks.put("Ayu", yellow);
                ranks.put("Bui", green);
                ranks.put("Mod", darkAqua);
                ranks.put("Adm", red);

                ChatFormatting color = ranks.get(mode);
                if (color != null) {
                    newName = getPrefix(mode, color) + newName;
                }
                
                if (cosmeticSpoof.getValue() && tagSpoof.getValue().length() == 3) {
                	newName = newName + red + " [" + tagSpoof.getValue() + "] ";
                }
            }

            text = text.replace(playerName, newName);
            event.setString(text);
        }

        if (checkFriends.getValue()) {
	        for (String friend : FriendAndTargetComponent.getFriends()) {
	        	if (text.contains(friend)) {
	        		text = text.replace(friend, protectFriends.getValue());
	        		event.setString(text);
	        	}
	        }
        }
    };
    
    private Set<Integer> getFurry() {
        Set<Integer> randomNumbers = new HashSet<>();
        Random random = new Random();
        int start = 140000;
        int randomNumber = start + random.nextInt(1000000);
        return randomNumbers;
    }
}