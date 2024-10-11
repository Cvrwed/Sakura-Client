package cc.unknown.event.impl.other;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptAttackEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.EntityLivingBase;

@AllArgsConstructor
@Getter
@Setter
public final class AttackEvent extends CancellableEvent {
    private EntityLivingBase target;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptAttackEvent(this);
    }
}