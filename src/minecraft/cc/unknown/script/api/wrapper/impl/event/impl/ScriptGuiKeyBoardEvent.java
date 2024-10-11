package cc.unknown.script.api.wrapper.impl.event.impl;

import cc.unknown.event.impl.input.GuiKeyBoardEvent;
import cc.unknown.script.api.wrapper.impl.event.CancellableScriptEvent;

public class ScriptGuiKeyBoardEvent extends CancellableScriptEvent<GuiKeyBoardEvent> {

    public ScriptGuiKeyBoardEvent(final GuiKeyBoardEvent wrappedEvent) {
        super(wrappedEvent);
    }
    
    public int getKeyCode() {
    	return this.wrapped.getKeyCode();
    }
    
    public char getCharacter() {
    	return this.wrapped.getCharacter();
    }

    @Override
    public String getHandlerName() {
        return "onGuiKeyBoard";
    }
}
