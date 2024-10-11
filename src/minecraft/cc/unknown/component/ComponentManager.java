package cc.unknown.component;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.Sakura;
import cc.unknown.component.impl.Component;
import cc.unknown.component.impl.event.EntityKillEventComponent;
import cc.unknown.component.impl.event.EntityTickComponent;
import cc.unknown.component.impl.event.MouseEventComponent;
import cc.unknown.component.impl.hud.DragComponent;
import cc.unknown.component.impl.patches.GuiClosePatchComponent;
import cc.unknown.component.impl.performance.ParticleDistanceComponent;
import cc.unknown.component.impl.player.BadPacketsComponent;
import cc.unknown.component.impl.player.BlinkComponent;
import cc.unknown.component.impl.player.FallDistanceComponent;
import cc.unknown.component.impl.player.GUIDetectionComponent;
import cc.unknown.component.impl.player.LastConnectionComponent;
import cc.unknown.component.impl.player.PingSpoofComponent;
import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.SecurityComponent;
import cc.unknown.component.impl.player.SelectorDetectionComponent;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.component.impl.player.TargetComponent;
import cc.unknown.component.impl.render.NotificationComponent;
import cc.unknown.component.impl.render.ProjectionComponent;
import cc.unknown.component.impl.universocraft.GameComponent;

/**
 * @author Alan
 * @since 01/27/2022
 */
public final class ComponentManager {

    private final Map<Class<Component>, Component> componentList = new HashMap<>();

    /**
     * Called on client start and when for some reason when we reinitialize
     */
    public void init() {
        this.add(new EntityKillEventComponent());
        this.add(new EntityTickComponent());
        this.add(new DragComponent());
        this.add(new GuiClosePatchComponent());
        this.add(new ParticleDistanceComponent());
        this.add(new BadPacketsComponent());
        this.add(new BlinkComponent());
        this.add(new GUIDetectionComponent());
        this.add(new LastConnectionComponent());
        this.add(new GameComponent());
        this.add(new PingSpoofComponent());
        this.add(new RotationComponent());
        this.add(new SelectorDetectionComponent());
        this.add(new Slot());
        this.add(new NotificationComponent());
        this.add(new ProjectionComponent());
        this.add(new SecurityComponent());
        this.add(new FallDistanceComponent());
        this.add(new TargetComponent());
        this.add(new MouseEventComponent());

        // Registers all components to the eventbus
        this.componentList.forEach((componentClass, component) -> Sakura.instance.getEventBus().register(component));
        this.componentList.forEach(((componentClass, component) -> component.onInit()));
    }

    public void add(final Component component) {
        this.componentList.put((Class<Component>) component.getClass(), component);
    }

    public <T extends Component> T get(final Class<T> clazz) {
        return (T) this.componentList.get(clazz);
    }
}