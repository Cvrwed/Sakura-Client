package cc.unknown.script.api.wrapper.impl.event.impl.player;

import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.script.api.wrapper.impl.ScriptEntityLiving;
import cc.unknown.script.api.wrapper.impl.event.CancellableScriptEvent;

/**
 * @author Auth
 * @since 10/07/2022
 */
public class ScriptAttackEvent extends CancellableScriptEvent<AttackEvent> {

    public ScriptAttackEvent(final AttackEvent wrappedEvent) {
        super(wrappedEvent);
    }

    public ScriptEntityLiving getTarget() {
        return new ScriptEntityLiving(this.wrapped.getTarget());
    }

    @Override
    public String getHandlerName() {
        return "onAttack";
    }
}
