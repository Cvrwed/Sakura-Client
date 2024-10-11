package cc.unknown.module.impl.movement;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.NumberValue;

/**
 * @author Alan Jr.
 * @since 9/17/2022
 */

@ModuleInfo(aliases = "Strafe", description = "Makes you always strafe, letting you move freely in air", category = Category.MOVEMENT)
public class Strafe extends Module {
    private NumberValue strength = new NumberValue("Strength", this, 100, 1, 100, 1);
    @EventLink
    public final Listener<StrafeEvent> onStrafe = event -> {
        MoveUtil.partialStrafePercent(strength.getValue().floatValue());
    };
}