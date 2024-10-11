package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.component.impl.player.FriendAndTargetComponent;
import cc.unknown.util.chat.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Alan
 * @since 10/19/2022
 */

public final class Friend extends Command {

	public Friend() {
		super("Make friends", "friend");
	}

	@Override
	public void execute(final String[] args) {
	    if (args.length == 2 && args[0].equalsIgnoreCase("friend") && args[1].equalsIgnoreCase("list")) {
	        ChatUtil.display(getFriendList());
	    } else if (args.length != 3) {
	        error(".friend <add/list/remove> <player>");
	    } else {
	        String action = args[1].toLowerCase();
	        String target = args[2];
	        boolean success = false;
	        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
	            if (!entityPlayer.getName().equalsIgnoreCase(target)) {
	                continue;
	            }
	            switch (action) {
	                case "add":
	                    FriendAndTargetComponent.addFriend(entityPlayer.getName());
	                    ChatUtil.display(String.format("Added %s to friends list", target));
	                    success = true;
	                    break;

	                case "remove":
	                    FriendAndTargetComponent.removeFriend(entityPlayer.getName());
	                    ChatUtil.display(String.format("Removed %s from friends list", target));
	                    success = true;
	                    break;
	            }
	            break;
	        }
	        if (!success) {
	            ChatUtil.display("That user could not be found.");
	        }
	    }
	}

	private String getFriendList() {
	    if (FriendAndTargetComponent.getFriends().isEmpty()) {
	        return "Your friend list is empty.";
	    }

	    StringBuilder message = new StringBuilder("Friend list:\n");
	    for (String friend : FriendAndTargetComponent.getFriends()) {
	        message.append("- ").append(friend).append("\n");
	    }
	    return message.toString();
	}
}