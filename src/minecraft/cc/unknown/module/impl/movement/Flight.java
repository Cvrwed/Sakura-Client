package cc.unknown.module.impl.movement;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.movement.flight.*;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;

/**
 * @author Auth (implementation)
 * @since 18/11/2021
 */

@ModuleInfo(aliases = "Flight", description = "Grants you the ability to fly", category = Category.MOVEMENT)
public class Flight extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new VanillaFlight("Vanilla", this))
            .add(new AirWalkFlight("Air Walk", this))
            .add(new VerusFlight("Verus", this))
            .add(new AirJumpFlight("Air Jump", this))
            .add(new MMCFlight("Minemen", this))
            .setDefault("Vanilla");

    private final BooleanValue disableOnTeleport = new BooleanValue("Disable on Teleport", this, false);
    private final BooleanValue fakeDamage = new BooleanValue("Fake Damage", this, false);

    private boolean teleported;

    @Override
    public void onEnable() {
        if (fakeDamage.getValue() && mc.player.ticksExisted > 1) {
            PlayerUtil.fakeDamage();
        }

        teleported = false;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
    }

    @EventLink
    public final Listener<TeleportEvent> onTeleport = event -> {
        if (disableOnTeleport.getValue()) {
            this.toggle();
        }
    };
}