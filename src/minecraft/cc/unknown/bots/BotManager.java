package cc.unknown.bots;

import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.TargetComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.WorldChangeEvent;

public class BotManager {
    private final HashMap<Object, ArrayList<Integer>> bots = new HashMap<>();

    public void init() {
        Sakura.instance.getEventBus().register(this);
    }

    @EventLink
    public final Listener<WorldChangeEvent> onWorldChange = event -> clear();

    public boolean contains(Entity target) {
        for (ArrayList<Integer> entities : bots.values()) {
            if (entities.contains(target.getEntityId())) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Object object, Entity target) {
        if (!bots.containsKey(object)) return false;
        return bots.get(object).contains(target.getEntityId());
    }

    public void add(Object object, Entity entity) {
        int id = entity.getEntityId();
        if (!bots.containsKey(object)) bots.put(object, new ArrayList<>());
        ArrayList<Integer> entities = bots.get(object);

        if (!entities.contains(id)) {
            entities.add(id);
        }
    }

    public void remove(Object object, Entity entity) {
        if (bots.containsKey(object)) {
            ArrayList<Integer> entities = bots.get(object);
            entities.remove((Object) entity.getEntityId());
        }
    }

    public void clear() {
        bots.clear();
    }

    public void clear(Object object) {
        if (!bots.containsKey(object)) return;

        bots.get(object).clear();
    }
}