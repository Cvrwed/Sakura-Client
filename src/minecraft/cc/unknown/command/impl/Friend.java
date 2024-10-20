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
	        switch (action) {
	        case "add":
	        	FriendAndTargetComponent.addFriend(target);
	        	ChatUtil.display(String.format("Added %s to friends list", target));
	        	break;

	        case "remove":
	        	FriendAndTargetComponent.removeFriend(target);
	        	ChatUtil.display(String.format("Removed %s from friends list", target));
	        	break;
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