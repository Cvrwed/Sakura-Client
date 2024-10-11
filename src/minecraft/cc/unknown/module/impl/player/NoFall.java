package cc.unknown.module.impl.player;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.player.nofall.LegitNofall;
import cc.unknown.module.impl.player.nofall.MatrixNoFall;
import cc.unknown.module.impl.player.nofall.PacketNoFall;
import cc.unknown.module.impl.player.nofall.PlaceNoFall;
import cc.unknown.module.impl.player.nofall.WatchdogNoFall;
import cc.unknown.value.impl.ModeValue;
import net.minecraft.client.renderer.entity.RendererLivingEntity;

/**
 * @author Alan
 * @since 23/10/2021
 */

@ModuleInfo(aliases = {"No Fall"}, description = "Reduces or eliminates fall damage", category = Category.PLAYER)
public class NoFall extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new PlaceNoFall("Place", this))
            .add(new LegitNofall("Legit", this))
            .add(new PacketNoFall("Packet", this))
            .add(new MatrixNoFall("Matrix", this))
            .add(new WatchdogNoFall("Watchdog", this))
            .setDefault("Legit");
}
