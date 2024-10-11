package cc.unknown.event.impl.other;

import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptServerJoinEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class ServerJoinEvent implements Event {
    public String ip;
    public int port;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptServerJoinEvent(this);
    }
}