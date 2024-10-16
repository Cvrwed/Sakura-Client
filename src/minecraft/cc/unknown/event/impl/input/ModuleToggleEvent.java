package cc.unknown.event.impl.input;

import cc.unknown.event.Event;
import cc.unknown.module.Module;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.input.ScriptModuleToggleEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class ModuleToggleEvent implements Event {
    private Module module;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptModuleToggleEvent(this);
    }
}