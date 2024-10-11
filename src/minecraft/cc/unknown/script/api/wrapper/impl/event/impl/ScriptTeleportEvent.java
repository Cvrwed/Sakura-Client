package cc.unknown.script.api.wrapper.impl.event.impl;

import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;

public class ScriptTeleportEvent extends ScriptEvent<TeleportEvent> {

    public ScriptTeleportEvent(final TeleportEvent wrappedEvent) {
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
    
    public float getYaw() {
    	return this.wrapped.getYaw();
    }
    
    public float getPitch() {
    	return this.wrapped.getPitch();
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
    
    public void setYaw(float yaw) {
    	this.wrapped.setYaw(yaw);
    }
    
    public void setPitch(float pitch) {
    	this.wrapped.setPitch(pitch);
    }

    @Override
    public String getHandlerName() {
        return "onTeleport";
    }
}
