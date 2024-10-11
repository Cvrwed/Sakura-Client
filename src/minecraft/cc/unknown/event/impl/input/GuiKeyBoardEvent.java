package cc.unknown.event.impl.input;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptGuiKeyBoardEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;

@AllArgsConstructor
@Getter
public final class GuiKeyBoardEvent extends CancellableEvent {
    private final int keyCode;
    private final char character;
    private final GuiScreen guiScreen;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptGuiKeyBoardEvent(this);
    }
}