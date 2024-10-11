package cc.unknown.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancellableEvent implements Event {

    private boolean cancelled;

    public void setCancelled() {
        this.cancelled = true;
    }
}