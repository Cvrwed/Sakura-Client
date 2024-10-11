package cc.unknown.script.api.wrapper.impl.event;

import cc.unknown.event.CancellableEvent;

/**
 * @author Auth
 * @since 9/07/2022
 */
public abstract class CancellableScriptEvent<T extends CancellableEvent> extends ScriptEvent<T> {

    public CancellableScriptEvent(final T wrappedEvent) {
        super(wrappedEvent);
    }

    public boolean isCancelled() {
        return this.wrapped.isCancelled();
    }

    public void setCancelled(final boolean cancelled) {
        this.wrapped.setCancelled(cancelled);
    }
}
