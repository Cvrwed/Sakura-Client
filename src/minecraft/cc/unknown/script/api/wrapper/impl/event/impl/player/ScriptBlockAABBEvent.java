package cc.unknown.script.api.wrapper.impl.event.impl.player;

import cc.unknown.event.impl.player.BlockAABBEvent;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * @author Auth
 * @since 9/07/2022
 */
public class ScriptBlockAABBEvent extends ScriptEvent<BlockAABBEvent> {

    public ScriptBlockAABBEvent(final BlockAABBEvent wrappedEvent) {
        super(wrappedEvent);
    }
    
    public World getWorld() {
    	return this.wrapped.getWorld();
    }
    
    public Block getBlock() {
    	return this.wrapped.getBlock();
    }
    
    public BlockPos getBlockPos() {
    	return this.wrapped.getBlockPos();
    }
    
    public AxisAlignedBB getMaskBoundingBox() {
    	return this.wrapped.getMaskBoundingBox();
    }
    
    public AxisAlignedBB getBoundingBox() {
    	return this.wrapped.getBoundingBox();
    }
    
    public void setBoundingBox(AxisAlignedBB axis) {
    	this.wrapped.setBoundingBox(axis);
    }

    @Override
    public String getHandlerName() {
        return "onBlockAABB";
    }
}
