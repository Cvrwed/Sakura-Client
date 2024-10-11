package cc.unknown.script.api;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.TargetComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.script.api.wrapper.impl.ScriptBlockPos;
import cc.unknown.script.api.wrapper.impl.ScriptEntityLiving;
import cc.unknown.script.api.wrapper.impl.ScriptWorld;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;

/**
 * @author Strikeless
 * @since 20.06.2022
 */
public class WorldAPI extends ScriptWorld {

    public WorldAPI() {
        super(MC.world);

        Sakura.instance.getEventBus().register(this);
    }

    @EventLink
    public final Listener<TickEvent> onTick = event -> {
        if (this.wrapped == null) {
            this.wrapped = MC.world;
        }
    };

    public ScriptEntityLiving[] getEntities() {
        final Object[] entityLivingBases = MC.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).toArray();
        final ScriptEntityLiving[] scriptEntities = new ScriptEntityLiving[entityLivingBases.length];

        for (int index = 0; index < entityLivingBases.length; index++) {
            scriptEntities[index] = new ScriptEntityLiving((EntityLivingBase) entityLivingBases[index]);
        }

        return scriptEntities;
    }

    public ScriptEntityLiving getTargetEntity(int range) {
        EntityLivingBase entityLivingBase = TargetComponent.getTarget(range);
        return entityLivingBase != null ? new ScriptEntityLiving(entityLivingBase) : null;
    }

    public void removeEntity(int id) {
        MC.world.removeEntityFromWorld(id);
    }

    public void removeEntity(ScriptEntityLiving entity) {
        removeEntity(entity.getEntityId());
    }

    public ScriptBlockPos newBlockPos(int x, int y, int z) {
        return new ScriptBlockPos(new BlockPos(x, y, z));
    }

    public String getBlockName(ScriptBlockPos blockPos) {
        return blockPos.getBlock().getName();
    }

}
