package cc.unknown.script.api.wrapper.impl.event.impl;

import cc.unknown.event.impl.other.MoveEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptMoveEvent extends ScriptEvent<MoveEvent> {

    public ScriptMoveEvent(final MoveEvent wrappedEvent) {
        super(wrappedEvent);
    }
    
    public double getPosX() {
    	return this.wrapped.getPosX();
    }
    
    public double getPosY() {
    	return this.wrapped.getPosY();
    }
    
    public double getPosZ() {
    	return this.wrapped.getPosZ();
    }
    
    public void setPosX(double posX) {
    	this.wrapped.setPosX(posX);
    }
    
    public void setPosY(double posY) {
    	this.wrapped.setPosY(posY);
    }
    
    public void setPosZ(double posZ) {
    	this.wrapped.setPosZ(posZ);
    }

    @Override
    public String getHandlerName() {
        return "onMove";
    }
}
