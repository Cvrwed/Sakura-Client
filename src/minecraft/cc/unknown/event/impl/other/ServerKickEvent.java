package cc.unknown.event.impl.other;

import java.util.List;

import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptServerKickEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class ServerKickEvent implements Event {
    public List<String> message;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptServerKickEvent(this);
    }
}