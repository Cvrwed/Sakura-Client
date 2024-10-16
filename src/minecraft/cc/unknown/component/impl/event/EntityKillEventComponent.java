package cc.unknown.component.impl.event;

import cc.unknown.Sakura;
import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.KillEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.util.interfaces.ThreadAccess;
import net.minecraft.entity.Entity;

public class EntityKillEventComponent extends Component implements ThreadAccess {

    Entity target = null;

    @EventLink(value = Priority.LOW)
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (target != null && !mc.world.loadedEntityList.contains(target)) {
            Sakura.instance.getEventBus().handle(new KillEvent(target));
            target = null;
        }
    };

    @EventLink(value = Priority.LOW)
    public final Listener<AttackEvent> onAttackEvent = event -> {
        target = event.getTarget();
    };

    @EventLink(value = Priority.LOW)
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        target = null;
    };
}
