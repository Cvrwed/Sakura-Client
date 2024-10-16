package cc.unknown.script.api.wrapper.impl.event.impl.player;

import cc.unknown.event.impl.player.PostStrafeEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

/**
 * @author Auth
 * @since 9/07/2022
 */
public class ScriptPostStrafeEvent extends ScriptEvent<PostStrafeEvent> {

    public ScriptPostStrafeEvent(final PostStrafeEvent wrappedEvent) {
        super(wrappedEvent);
    }

    @Override
    public String getHandlerName() {
        return "onPostStrafe";
    }
}
