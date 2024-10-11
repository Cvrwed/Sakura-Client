package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * @author Patrick
 * @since 10/19/2021
 */

@ModuleInfo(aliases = {"Full Bright"}, description = "Prevents world darkness", category = Category.VISUALS)
public final class FullBright extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Gamma"))
            .add(new SubMode("Effect"))
            .setDefault("Gamma");
    
    private float oldGamma;
    
    @EventLink
    public final Listener<TickEvent> onTick = event -> {
    	if (mode.getValue().getName().equalsIgnoreCase("Effect")) {
    		mc.player.addPotionEffect(new PotionEffect(Potion.nightVision.id, Integer.MAX_VALUE, 1));
    	} 

    	if (mode.getValue().getName().equalsIgnoreCase("Gamma")) {
    		mc.gameSettings.gammaSetting = 100.0F;
    	}
    };
    
    @Override
    public void onEnable() {
        oldGamma = mc.gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = oldGamma;

        if (mc.player.isPotionActive(Potion.nightVision)) {
            mc.player.removePotionEffect(Potion.nightVision.id);
        }
    }
}