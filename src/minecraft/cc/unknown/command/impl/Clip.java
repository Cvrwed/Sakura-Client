package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.util.chat.ChatUtil;

/**
 * @author Auth
 * @since 3/02/2022
 */

public final class Clip extends Command {

    public Clip() {
        super("Clips you the given amount of blocks in the given direction", "clip");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length <= 1 || args[1].isEmpty()) {
            warning(".clip <up/down/forward/back/left/right> <amount>");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "vclip": {
                final double amount = Double.parseDouble(args[1]);

                mc.player.setPosition(mc.player.posX, mc.player.posY + amount, mc.player.posZ);
                ChatUtil.display("Clipped you " + (amount > 0 ? "up" : "down") + " " + Math.abs(amount) + " blocks.");
                break;
            }

            case "hclip": {
                final double amount = Double.parseDouble(args[1]);

                final double yaw = Math.toRadians(mc.player.rotationYaw);
                final double x = Math.sin(yaw) * amount;
                final double z = Math.cos(yaw) * amount;

                mc.player.setPosition(mc.player.posX - x, mc.player.posY, mc.player.posZ + z);
                break;
            }

            case "clip": {
                if (args.length <= 2 || args[2].isEmpty()) {
                	warning(".clip <up/down/forward/back/left/right> <amount>");
                    return;
                }

                switch (args[1]) {
                    case "upward":
                    case "upwards":
                    case "up": {
                        final double amount = Double.parseDouble(args[2]);

                        mc.player.setPosition(mc.player.posX, mc.player.posY + amount, mc.player.posZ);
                        break;
                    }

                    case "downward":
                    case "downwards":
                    case "down": {
                        final double amount = Double.parseDouble(args[2]);

                        mc.player.setPosition(mc.player.posX, mc.player.posY - amount, mc.player.posZ);
                        break;
                    }

                    case "forwards":
                    case "forward": {
                        final double amount = Double.parseDouble(args[2]);

                        final double yaw = Math.toRadians(mc.player.rotationYaw);
                        final double x = Math.sin(yaw) * amount;
                        final double z = Math.cos(yaw) * amount;

                        mc.player.setPosition(mc.player.posX - x, mc.player.posY, mc.player.posZ + z);
                        break;
                    }

                    case "backwards":
                    case "backward":
                    case "back": {
                        final double amount = Double.parseDouble(args[2]);

                        final double yaw = Math.toRadians(mc.player.rotationYaw);
                        final double x = Math.sin(yaw) * amount;
                        final double z = Math.cos(yaw) * amount;

                        mc.player.setPosition(mc.player.posX + x, mc.player.posY, mc.player.posZ - z);
                        break;
                    }

                    case "left": {
                        final double amount = Double.parseDouble(args[2]);

                        final double yaw = Math.toRadians(mc.player.rotationYaw - 90);
                        final double x = Math.sin(yaw) * amount;
                        final double z = Math.cos(yaw) * amount;

                        mc.player.setPosition(mc.player.posX - x, mc.player.posY, mc.player.posZ + z);
                        break;
                    }

                    case "right": {
                        final double amount = Double.parseDouble(args[2]);

                        final double yaw = Math.toRadians(mc.player.rotationYaw + 90);
                        final double x = Math.sin(yaw) * amount;
                        final double z = Math.cos(yaw) * amount;

                        mc.player.setPosition(mc.player.posX - x, mc.player.posY, mc.player.posZ + z);
                        break;
                    }
                }
                break;
            }
        }
    }
}
