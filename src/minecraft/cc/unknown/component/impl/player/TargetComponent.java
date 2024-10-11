package cc.unknown.component.impl.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Sakura;
import cc.unknown.component.impl.Component;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.time.StopWatch;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

public class TargetComponent extends Component implements Accessor {
    private static final HashMap<Class<?>, List<EntityLivingBase>> targetMap = new HashMap<>();
    private static final HashMap<Class<?>, Integer> entityAmountMap = new HashMap<>();
    private static final HashMap<Class<?>, StopWatch> timerMap = new HashMap<>();
    private static KillAura aura;

    public static void handler(List<EntityLivingBase> entities, Class<?> module) {
        List<EntityLivingBase> targetList = new ArrayList<>(entities);
        targetMap.put(module, targetList);
    }

    public static EntityLivingBase getTarget(double range) {
        return getTargets(range).stream().findFirst().orElse(null);
    }

    public static List<EntityLivingBase> getTargets(double range) {
        if (aura == null) {
            aura = Sakura.instance.getModuleManager().get(KillAura.class);
        }

        return getTargets(aura.getClass(), aura.player.getValue(), aura.invisibles.getValue(), aura.animals.getValue(), aura.mobs.getValue(), aura.teams.getValue()).stream().filter(entity -> mc.player.getDistanceToEntity(entity) <= range).collect(Collectors.toList());
    }

    public static List<EntityLivingBase> getTargets(Class<?> module, double range, boolean players, boolean invisibles, boolean animals, boolean mobs, boolean teams) {
        return getTargets(module, players, invisibles, animals, mobs, teams).stream().filter(entity -> mc.player.getDistanceToEntity(entity) <= range).collect(Collectors.toList());
    }

    public static List<EntityLivingBase> getTargets() {
        if (aura == null) {
            aura = Sakura.instance.getModuleManager().get(KillAura.class);
        }

        return getTargets(aura.getClass(), aura.player.getValue(), aura.invisibles.getValue(), aura.animals.getValue(), aura.mobs.getValue(), aura.teams.getValue());
    }

    public static List<EntityLivingBase> getTargets(Class<?> module, boolean players, boolean invisibles, boolean animals, boolean mobs, boolean teams) {
        if (aura == null) {
            aura = Sakura.instance.getModuleManager().get(KillAura.class);
        }
        
            List<EntityLivingBase> startingTargets = mc.world.loadedEntityList.stream()
            .filter(entity -> entity instanceof EntityLivingBase)
            .filter(entity -> entity != mc.getRenderViewEntity())
            .filter(entity -> !FriendAndTargetComponent.isFriend(entity.getName()))
            .filter(entity -> !Sakura.instance.getBotManager().contains(entity))
            .filter(entity -> !(entity instanceof EntityArmorStand))
            .filter(entity -> !(entity instanceof EntityVillager))
            .filter(entity -> !entity.getName().contains("[NPC]"))
            .filter(entity -> !(entity instanceof EntityPlayer) || players)
            .filter(entity -> !(entity instanceof EntityPlayer && PlayerUtil.isTeam((EntityPlayer) entity, aura.scoreboardCheckTeam.getValue(), aura.checkArmorColor.getValue()) && teams))
            .filter(entity -> !(entity.isInvisible() && !invisibles))
            .filter(entity -> !(entity instanceof EntityAnimal && !animals))
            .filter(entity -> mobs || !(entity instanceof EntityMob || entity instanceof EntitySlime))
            .map(entity -> (EntityLivingBase) entity)
            .collect(Collectors.toList());

            if (startingTargets.isEmpty()) {
                return new ArrayList<>();
            }

            handler(startingTargets, module);

            //entityAmountMap.put(module, mc.world.loadedEntityList.size());
        return targetMap.getOrDefault(module, new ArrayList<>()).stream().filter(entity -> mc.world.loadedEntityList.contains(entity)).collect(Collectors.toList());
    }
}