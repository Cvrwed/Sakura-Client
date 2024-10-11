package cc.unknown.module.impl.movement;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.movement.speed.*;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;

/**
 * @author Patrick (implementation)
 * @since 10/19/2021
 */

@ModuleInfo(aliases = "Speed", description = "Increases your movement speed", category = Category.MOVEMENT)
public class Speed extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new VanillaSpeed("Vanilla", this))
            .add(new NCPSpeed("NCP", this))
            .add(new BlocksMCSpeed("BlocksMC", this))
            .add(new SparkySpeed("Sparky", this))
            .add(new GrimSpeed("Grim", this))
            .add(new LibrecraftSpeed("Librecraft", this))
            .add(new LowSpeed("Slow Hop", this))
            .add(new WatchdogSpeed("Watchdog", this))
            .setDefault("Vanilla");

    private final BooleanValue disableOnTeleport = new BooleanValue("Disable on Teleport", this, false);
    private final BooleanValue stopOnDisable = new BooleanValue("Stop on Disable", this, false);

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;

        if (stopOnDisable.getValue()) {
            MoveUtil.stop();
        }
    }

    @EventLink
    public final Listener<TeleportEvent> onTeleport = event -> {
        if (disableOnTeleport.getValue()) {
            this.toggle();
        }
    };
}