package cc.unknown.module.impl.movement.flight;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.module.impl.movement.Flight;
import cc.unknown.value.Mode;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public final class AirWalkFlight extends Mode<Flight> {

    public AirWalkFlight(String name, Flight parent) {
        super(name, parent);
    }

    @EventLink
    public final Listener<BlockAABBEvent> onBlockAABB = event -> {
        // Sets The Bounding Box To The Players Y Position.
        if (event.getBlock() instanceof BlockAir && !mc.player.isSneaking() && mc.player.ticksSinceTeleport > 2) {
            final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

            if (y < mc.player.posY) {
                event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
            }
        }
    };
}
