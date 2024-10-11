package cc.unknown.command.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import cc.unknown.command.Command;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.file.config.ConfigFile;
import cc.unknown.util.file.config.ConfigManager;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;

public final class Config extends Command {

    public Config() {
        super("Allows you to save/load configuration files", "cfg");
    }

    @Override
    public void execute(final String[] args) {
        final ConfigManager configManager = getInstance().getConfigManager();
        final String command = args[1].toLowerCase();

        switch (args.length) {
            case 3:
                final String name = args[2];

                switch (command) {
                    case "load":
                        configManager.update();

                        final ConfigFile config = configManager.get(name);

                        if (config != null) {
                            CompletableFuture.runAsync(() -> {
                                if (config.read()) {
                                    ChatUtil.display("Loaded %s config file!", name);
                                }
                            });
                        }
                        break;

                    case "save":
                    case "create":
                        if (name.equalsIgnoreCase("latest")) {
                            return;
                        }

                        CompletableFuture.runAsync(() -> {
                            configManager.set(name);

                            success("Saved config file!");
                        });
                        break;
                        
                    case "remove":
                        CompletableFuture.runAsync(() -> {
                            ConfigFile configToRemove = configManager.get(name);
                            if (configToRemove != null && configToRemove.getFile().delete()) {
                                configManager.update();
                                ChatUtil.display("Removed config file: %s", name);
                            } else {
                                ChatUtil.display("Failed to remove config file: %s", name);
                            }
                        });
                        break;
                    default:
                        warning("Usage: .config save/load/list/folder");
                        break;
                }
                break;

            case 2:
                switch (command) {
                    case "list":
                        warning("Click on the config you want to load.");

                        configManager.update();

                        configManager.forEach(configFile -> {
                            final String configName = configFile.getFile().getName().replace(".json", "");
                            final String configCommand = ".config load " + configName;
                            final String color = getTheme().getChatAccentColor().toString();

                            final ChatComponentText chatText = new ChatComponentText(color + "> " + configName);
                            final ChatComponentText hoverText = new ChatComponentText(String.format("Click to load config %s", configName));

                            chatText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, configCommand))
                                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

                            mc.player.addChatMessage(chatText);
                        });
                        break;
                        
                    case "open":
                    case "folder":
                        try {
                            Desktop desktop = Desktop.getDesktop();
                            File dirToOpen = new File(String.valueOf(ConfigManager.CONFIG_DIRECTORY));
                            desktop.open(dirToOpen);
                            success("Opened config folder");
                        } catch (IllegalArgumentException | IOException iae) {
                            ChatUtil.display("Config file not found!");
                        }
                        break;
                }
                break;
        }
    }
}
