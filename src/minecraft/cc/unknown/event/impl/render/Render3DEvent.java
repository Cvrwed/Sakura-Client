package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.render.ScriptRender3DEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class Render3DEvent implements Event {
    private final float partialTicks;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptRender3DEvent(this);
    }
}
