package cc.unknown.event.impl.render;

import cc.unknown.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class SwingAnimationEvent extends CancellableEvent {
    private int animationEnd;
}
