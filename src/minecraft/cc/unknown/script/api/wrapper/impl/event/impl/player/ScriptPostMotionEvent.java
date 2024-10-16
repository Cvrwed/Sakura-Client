package cc.unknown.script.api.wrapper.impl.event.impl.player;

import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptPostMotionEvent extends ScriptEvent<PostMotionEvent> {

    public ScriptPostMotionEvent(final PostMotionEvent wrappedEvent) {
        super(wrappedEvent);
    }

    @Override
    public String getHandlerName() {
        return "onPostMotion";
    }
}
