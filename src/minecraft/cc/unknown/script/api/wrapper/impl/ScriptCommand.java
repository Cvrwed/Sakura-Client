package cc.unknown.script.api.wrapper.impl;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.script.api.wrapper.ScriptHandlerWrapper;

/**
 * @author Strikeless
 * @since 15.05.2022
 */
public final class ScriptCommand extends ScriptHandlerWrapper<Command> {

    public ScriptCommand(final Command wrapped) {
        super(wrapped);
    }

    public void unregister() {
    	Sakura.instance.getCommandManager().getCommandList().remove(this.wrapped);
    }

    // TODO: Make command execution again

    public String getName() {
        return this.wrapped.getExpressions()[0];
    }

    public String getDescription() {
        return this.wrapped.getDescription();
    }
}
