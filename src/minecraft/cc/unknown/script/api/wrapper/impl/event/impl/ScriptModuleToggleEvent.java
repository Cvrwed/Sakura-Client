package cc.unknown.script.api.wrapper.impl.event.impl;

import cc.unknown.event.impl.other.ModuleToggleEvent;
import cc.unknown.script.api.wrapper.impl.ScriptModule;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptModuleToggleEvent extends ScriptEvent<ModuleToggleEvent> {

    public ScriptModuleToggleEvent(ModuleToggleEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public ScriptModule getModule() {
        return new ScriptModule(wrapped.getModule());
    }

    @Override
    public String getHandlerName() {
        return "onModuleToggle";
    }
}
