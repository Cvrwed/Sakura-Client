package cc.unknown.module.impl.ghost;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.RightClickEvent;
import cc.unknown.event.impl.render.MouseOverEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.RayCastUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;

/**
 * @author Alan
 * @since 29/01/2021
 */

@ModuleInfo(aliases = "Hit Box", description = "Expands other entities hitboxes", category = Category.GHOST)
public class HitBox extends Module {
    public final NumberValue expand = new NumberValue("Expand Amount", this, 0, 0, 6, 0.01);
    private final BooleanValue effectRange = new BooleanValue("Effect range", this, true);

    @EventLink
    public final Listener<MouseOverEvent> onMouseOver = event -> {
        event.setExpand(this.expand.getValue().floatValue());

        if (!this.effectRange.getValue()) {
            event.setRange(event.getRange() - expand.getValue().doubleValue());
        }
    };

    @EventLink
    public final Listener<RightClickEvent> onRightClick = event ->
            mc.objectMouseOver = RayCastUtil.rayCast(RotationComponent.rotations, 4.5);
}