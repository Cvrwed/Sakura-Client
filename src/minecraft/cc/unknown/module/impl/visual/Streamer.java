package cc.unknown.module.impl.visual;

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
import cc.unknown.value.impl.StringValue;

@ModuleInfo(aliases = "Streamer", description = "Hides your name", category = Category.VISUALS)
public final class Streamer extends Module {

    public final StringValue replacement = new StringValue("Replacement", this, "You");
    private final BooleanValue protectFriends = new BooleanValue("Protect Friends", this, true);

    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<RenderTextEvent> onRenderText = event -> {
        assert mc.player != null;
        String text = event.getString();
        String playerName = mc.player.getName();
        
        if (text.startsWith("/") || text.startsWith(Sakura.instance.getCommandManager().getPrefix())) {
        	return;
        }
        
        if (text.contains(playerName)) {
            text = text.replace(playerName, replacement.getValue());
            event.setString(text);
        }

        if (protectFriends.getValue()) {
            for (String friend : FriendAndTargetComponent.getFriends()) {
                if (text.contains(friend)) {
                    text = text.replace(friend, "Friend");
                    event.setString(text);
                }
            }
        }
    };
}