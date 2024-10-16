package cc.unknown.event.impl.player;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.player.ScriptSlowDownEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlowDownEvent extends CancellableEvent {
    private float strafeMultiplier;
    private float forwardMultiplier;
    private boolean sprint;
    
	public SlowDownEvent(float strafeMultiplier, float forwardMultiplier) {
		this.strafeMultiplier = strafeMultiplier;
		this.forwardMultiplier = forwardMultiplier;
	}
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptSlowDownEvent(this);
    }
}
