package cc.unknown.module.impl.player;

import cc.unknown.component.impl.player.BadPacketsComponent;
import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;

@ModuleInfo(aliases = {"Anti Fire Ball"}, description = "Prevents you from getting hit by fireballs", category = Category.PLAYER)
public class AntiFireBall extends Module {

	private final NumberValue range = new NumberValue("Range", this, 6.0, 4.0, 6.0, 0.1);
    private final BoundsNumberValue rotationSpeed = new BoundsNumberValue("Rotation Speed", this, 5, 10, 0, 10, 1);
    private final BooleanValue rotate = new BooleanValue("Rotate", this, true);

    @EventLink(value = Priority.VERY_HIGH)
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityFireball && entity.getDistanceToEntity(mc.player) < range.getValue().doubleValue()) {
                if (this.rotate.getValue()) {
                    RotationComponent.setRotations(RotationUtil.calculate(entity), rotationSpeed.getValue().intValue(), MovementFix.SILENT);
                }
                PacketUtil.sendNoEvent(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                PacketUtil.sendNoEvent(new C0APacketAnimation());
                break;
            }
        }
    };
}