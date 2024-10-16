package cc.unknown.script.api.wrapper.impl.event.impl.input;

import cc.unknown.event.impl.input.MouseInputEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptMouseInputEvent extends ScriptEvent<MouseInputEvent> {

    public ScriptMouseInputEvent(final MouseInputEvent wrappedEvent) {
        super(wrappedEvent);
    }
    
    public int getMouseCode() {
    	return this.wrapped.getMouseCode();
    }

    @Override
    public String getHandlerName() {
        return "onMouseInput";
    }
}
