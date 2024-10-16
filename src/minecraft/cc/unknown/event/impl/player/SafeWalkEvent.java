package cc.unknown.event.impl.player;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class SafeWalkEvent implements Event {
    private double height;
}
