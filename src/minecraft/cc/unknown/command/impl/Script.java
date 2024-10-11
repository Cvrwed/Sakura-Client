package cc.unknown.command.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.script.ScriptManager;
import cc.unknown.util.chat.ChatUtil;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public final class Script extends Command {

    public Script() {
        super("Script", "script", "scripts", "js");
    }

    @Override
    public void execute(final String[] args) {
        final String action = args[1].toLowerCase(Locale.getDefault());

        final ScriptManager scriptManager = Sakura.instance.getScriptManager();

        final cc.unknown.script.Script script;
        if (args.length > 3) {
            script = scriptManager.getScript(args[2]);
            if (script == null) {
                ChatUtil.display("File not found", args[2]);
                return;
            }
        } else script = null;

        try {
            switch (action) {
                case "load": {
                    if (script == null) scriptManager.loadScripts();
                    else script.load();
                    break;
                }

                case "reload": {
                    Sakura.instance.getScriptManager().reloadScripts();
                    Sakura.instance.getClickGui().moduleList = new ConcurrentLinkedQueue<>();
                    break;
                }

                case "unload": {
                    if (script == null) scriptManager.unloadScripts();
                    else script.unload();
                    break;
                }
                
                case "api": {
                    try {
                    	Desktop desktop = Desktop.getDesktop();
                    	if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    		URI youtubeLink = new URI("https://riseclients-organization.gitbook.io/rise-6-scripting-api");
                    		desktop.browse(youtubeLink);
                    	}
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case "folder": {
                    try {
                        Desktop desktop = Desktop.getDesktop();
                        File dirToOpen = new File(String.valueOf(ScriptManager.SCRIPT_DIRECTORY));
                        desktop.open(dirToOpen);
                    } catch (IllegalArgumentException | IOException iae) {
                        ChatUtil.display("Script directory not found");
                    }
                    return;
                }
            }

            ChatUtil.display(
                    "Successfully " + action + "ed "
                            + (script == null ? "all scripts" : "\"" + script.getName() + "\"")
                            + "."
            );
        } catch (final Exception ex) {
            ex.printStackTrace();
            ChatUtil.display("Failed to " + action + " a script. Stacktrace printed.");
        }
    }
}