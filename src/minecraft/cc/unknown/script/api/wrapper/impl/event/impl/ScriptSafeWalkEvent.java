package cc.unknown.script.api.wrapper.impl.event.impl;

import cc.unknown.event.impl.motion.SafeWalkEvent;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptSafeWalkEvent extends ScriptEvent<SafeWalkEvent> {

    public ScriptSafeWalkEvent(final SafeWalkEvent wrappedEvent) {
        super(wrappedEvent);
    }
    
    public double getHeight() {
    	return this.wrapped.getHeight();
    }

    @Override
    public String getHandlerName() {
        return "onSafeWalk";
    }
}
