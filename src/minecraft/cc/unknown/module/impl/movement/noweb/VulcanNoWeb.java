package cc.unknown.module.impl.movement.noweb;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.module.impl.movement.NoWeb;
import cc.unknown.value.Mode;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class VulcanNoWeb extends Mode<NoWeb> {

	public VulcanNoWeb(String name, NoWeb parent) {
		super(name, parent);
	}

    @EventLink
    public final Listener<BlockAABBEvent> onBlockAABB = event -> {
        if (event.getBlock() == Blocks.web) {
            BlockPos pos = event.getBlockPos();
            event.setBoundingBox(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));
        }
    };
    
    
}
