package cc.unknown.event.impl.input;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.input.ScriptChatInputEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class ChatInputEvent extends CancellableEvent {
    private String message;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptChatInputEvent(this);
    }
}