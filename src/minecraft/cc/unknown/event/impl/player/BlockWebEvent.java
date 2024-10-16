package cc.unknown.event.impl.player;

import cc.unknown.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

@AllArgsConstructor
@Getter
public class BlockWebEvent extends CancellableEvent {
	private final BlockPos blockPos;
	private final IBlockState blockState;
}
