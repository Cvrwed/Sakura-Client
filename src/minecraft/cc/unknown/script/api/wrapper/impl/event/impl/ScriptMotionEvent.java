package cc.unknown.script.api.wrapper.impl.event.impl;

import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.script.api.wrapper.impl.event.CancellableScriptEvent;

/**
 * @author Auth
 * @since 9/07/2022
 */
public class ScriptMotionEvent extends CancellableScriptEvent<MotionEvent> {

    public ScriptMotionEvent(final MotionEvent wrappedEvent) {
        super(wrappedEvent);
    }
    
    public boolean isPre() {
    	return this.wrapped.isPre();
    }
    
    public boolean isPost() {
    	return this.wrapped.isPost();
    }

    public void setPosX(final double posX) {
        this.wrapped.setPosX(posX);
    }

    public void setPosY(final double posY) {
        this.wrapped.setPosY(posY);
    }

    public void setPosZ(final double posZ) {
        this.wrapped.setPosZ(posZ);
    }

    public void setYaw(final float yaw) {
        this.wrapped.setYaw(yaw);
    }

    public void setPitch(final float pitch) {
        this.wrapped.setPitch(pitch);
    }

    public void setOnGround(final boolean onGround) {
        this.wrapped.setOnGround(onGround);
    }

    public void setSprinting(final boolean sprinting) {
        this.wrapped.setSprinting(sprinting);
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

    public boolean isOnGround() {
        return this.wrapped.isOnGround();
    }

    public boolean isSprinting() {
        return this.wrapped.isSprinting();
    }

    @Override
    public String getHandlerName() {
        return "onMotion";
    }
}
