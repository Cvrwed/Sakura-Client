package cc.unknown.event.impl.other;

import cc.unknown.event.CancellableEvent;
import cc.unknown.event.Event;
import cc.unknown.script.api.wrapper.impl.event.ScriptEvent;
import cc.unknown.script.api.wrapper.impl.event.impl.ScriptBlockAABBEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@AllArgsConstructor
@Getter
@Setter
public class BlockAABBEvent extends CancellableEvent {
    private final World world;
    private final Block block;
    private final BlockPos blockPos;
    private AxisAlignedBB boundingBox;
    private final AxisAlignedBB maskBoundingBox;
    
    @Override
    public ScriptEvent<? extends Event> getScriptEvent() {
        return new ScriptBlockAABBEvent(this);
    }
}
