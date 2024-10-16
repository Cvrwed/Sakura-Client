package cc.unknown.event.impl.player;

import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.player.ScriptPostMotionEvent;

public class PostMotionEvent implements Event {
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptPostMotionEvent(this);
    }
}
