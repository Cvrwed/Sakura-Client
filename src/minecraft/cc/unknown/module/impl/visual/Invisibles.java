package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.potion.Potion;

@ModuleInfo(aliases = "Invisibles", description = "Shows invisible objects or entities", category = Category.VISUALS)
public final class Invisibles extends Module {
    
	private final BooleanValue players = new BooleanValue("Show players", this, false);
    public final BooleanValue barriers = new BooleanValue("Show barriers", this, false);
    
    private double offset = 0.5;
    
    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
    	if (players.getValue()) {
            mc.world.playerEntities.stream().forEach(player -> {
            	player.removePotionEffect(Potion.invisibility.getId());
                player.setInvisible(false);   
            });
    	}
    };
}
