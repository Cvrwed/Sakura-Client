package cc.unknown.event.impl.other;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptMoveEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class MoveEvent extends CancellableEvent {
    private double posX, posY, posZ;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptMoveEvent(this);
    }
}
