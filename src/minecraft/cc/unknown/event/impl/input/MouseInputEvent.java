package cc.unknown.event.impl.input;

import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.input.ScriptMouseInputEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class MouseInputEvent implements Event {
    private final int mouseCode;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptMouseInputEvent(this);
    }
}
