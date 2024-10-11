package cc.unknown.event.impl.other;

import cc.unknown.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@AllArgsConstructor
@Getter
@Setter
public final class BlockDamageEvent extends CancellableEvent {
    private EntityPlayerSP player;
    private World world;
    private BlockPos blockPos;
}