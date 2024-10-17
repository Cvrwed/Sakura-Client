package cc.unknown.script.api.wrapper.impl.event.impl.other;

import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptWorldChangeEvent extends ScriptEvent<WorldChangeEvent> {

    public ScriptWorldChangeEvent(final WorldChangeEvent wrappedEvent) {
        super(wrappedEvent);
    }

    @Override
    public String getHandlerName() {
        return "onWorldChange";
    }
}
