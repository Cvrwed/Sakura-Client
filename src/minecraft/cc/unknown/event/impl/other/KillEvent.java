package cc.unknown.event.impl.other;

import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.other.ScriptKillEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@AllArgsConstructor
@Getter
@Setter
public final class KillEvent implements Event {
    private Entity entity;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptKillEvent(this);
    }
}