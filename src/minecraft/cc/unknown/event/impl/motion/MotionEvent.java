package cc.unknown.event.impl.motion;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptMotionEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class MotionEvent extends CancellableEvent {
    @Getter @Setter private double posX;
    @Getter @Setter private double posY;
    @Getter @Setter private double posZ;
    @Getter @Setter private float yaw;
    @Getter @Setter private float pitch;
    @Getter @Setter private boolean onGround;
    @Getter @Setter private boolean isSprinting;
    
    private Type type;
    
    public MotionEvent(Type type) {
        this.type = type;
    }

    public boolean isPost() {
        return type == Type.Post;
    }
    
    public boolean isPre() {
        return type == Type.Pre;
    }
    
    public enum Type {
        Pre, Post;
    }
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptMotionEvent(this);
    }
}