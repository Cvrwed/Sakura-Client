package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import cc.unknown.util.vector.Vector2f;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class LookEvent implements Event {
    private Vector2f rotation;
}
