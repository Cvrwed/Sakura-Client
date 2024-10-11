package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.util.MovingObjectPosition;

@Getter
@Setter
public class MouseOverEvent implements Event {
    private double range;
    private float expand;
    private MovingObjectPosition movingObjectPosition;
    
	public MouseOverEvent(double range, float expand) {
		this.range = range;
		this.expand = expand;
	}
}
