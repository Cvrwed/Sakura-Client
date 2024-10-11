package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.component.impl.player.FriendAndTargetComponent;
import cc.unknown.util.chat.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Bebebebebe
 * @since 3/24/2024
 */

public final class Target extends Command {

    public Target() {
        super("Make enemies", "target");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length != 3) {
            error(".target <add/remove> <player>");
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
                        FriendAndTargetComponent.addTarget(entityPlayer.getName());
                        ChatUtil.display(String.format("Added %s to target list", target));
                        success = true;
                        break;

                    case "remove":
                        FriendAndTargetComponent.removeTarget(entityPlayer.getName());
                        ChatUtil.display(String.format("Removed %s from target list", target));
                        success = true;
                        break;
                }
                break;
            }
            if (!success) {
                error("That user could not be found.");
            }
        }
    }
}