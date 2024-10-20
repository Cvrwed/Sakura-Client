package cc.unknown.module.impl.visual;

import static cc.unknown.util.render.RenderUtil.horizontalGradient;
import static cc.unknown.util.render.RenderUtil.isInViewFrustrum;
import static cc.unknown.util.render.RenderUtil.rectangle;
import static cc.unknown.util.render.RenderUtil.verticalGradient;

import java.awt.Color;

import javax.vecmath.Vector4d;

import cc.unknown.Sakura;
import cc.unknown.component.impl.render.ProjectionComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "Extra Sensory Perception", description = "Renders all players into a shader and displays it", category = Category.VISUALS)
public final class ExtraSensoryPerception extends Module {
	
	private final ModeValue mode = new ModeValue("Projection", this)
			.add(new SubMode("2D"))
			.add(new SubMode("3D"))
			.add(new SubMode("None"))
			.setDefault("2D");
	
    public final ModeValue box = new ModeValue("Box Mode", this, () -> !mode.is("2D"))
            .add(new SubMode("Standard"))
            .add(new SubMode("Infill"))
            .add(new SubMode("None"))
            .setDefault("Standard");
    
    private final ModeValue healthbBar = new ModeValue("Health Bar", this)
            .add(new SubMode("Standard"))
            .add(new SubMode("Gradient"))
            .add(new SubMode("None"))
            .setDefault("None");
        
    private double offset = 0.5;
    
    @EventLink
    public final Listener<Render2DEvent> onRender2D = event -> {
		if (isClickGui()) return;
		
        for (EntityPlayer player : mc.world.playerEntities) {
            if (mc.getRenderManager() == null || !isInViewFrustrum(player) || player.isDead || Sakura.instance.getBotManager().contains(player) || player == mc.player) {
                continue;
            }

            Vector4d pos = ProjectionComponent.get(player);

            if (pos == null) {
                continue;
            }

            if (!box.is("None")) {
            	final Vector2d first = new Vector2d(0, 0), second = new Vector2d(0, 500);

            	//background
            	rectangle(pos.x, pos.y, pos.z - pos.x, 1.5, Color.BLACK); // Top
            	rectangle(pos.x, pos.y, 1.5, pos.w - pos.y + 1.5, Color.BLACK); // Left
            	rectangle(pos.z, pos.y, 1.5, pos.w - pos.y + 1.5, Color.BLACK); // Right
            	rectangle(pos.x, pos.w, pos.z - pos.x, 1.5, Color.BLACK); // Bottom

            	//main esp
            	horizontalGradient(pos.x + offset, pos.y + offset, pos.z - pos.x, 0.5, // Top
            			this.getTheme().getAccentColor(first), this.getTheme().getAccentColor(second));
            	verticalGradient(pos.x + offset, pos.y + offset, 0.5, pos.w - pos.y + 0.5, // Left
            			this.getTheme().getAccentColor(first), this.getTheme().getAccentColor(second));
            	verticalGradient(pos.z + offset, pos.y + offset, 0.5, pos.w - pos.y + 0.5, // Right
            			this.getTheme().getAccentColor(second), this.getTheme().getAccentColor(first));
            	horizontalGradient(pos.x + offset, pos.w + offset, pos.z - pos.x, 0.5, // Bottom
            			this.getTheme().getAccentColor(second), this.getTheme().getAccentColor(first));

            	//optional modes
            	switch (box.getValue().getName()) {
            	case "Infill":
            		rectangle(pos.z, pos.y + 1.5, pos.x - pos.z + 1.5, pos.w - pos.y - 1.5, getTheme().getBackgroundShade());
            		break;
            	}
            }
            
            double height = pos.w - pos.y + 1;
            double health = player.getHealth() / player.getMaxHealth();
            
            switch (healthbBar.getValue().getName()) {
            case "Gradient":
            	if (health > 1) health = 1;
            	RenderUtil.rectangle(pos.x - 3, pos.y, 1.5, pos.w - pos.y + 1.5, Color.BLACK);
            	if (health > 0.5) {
            		height /= 2;
            		RenderUtil.verticalGradient(pos.x - 2.5, pos.y + offset + (height - height * (1 - (1 - health) * 2)), offset, height * (1 - (1 - health) * 2), ColorUtil.mixColors(Color.green, Color.yellow, health), Color.yellow);
            		RenderUtil.verticalGradient(pos.x - 2.5, pos.y + height, offset, height, Color.yellow, Color.red);
            	} else {
            		RenderUtil.verticalGradient(pos.x - 2.5, pos.y + height + height * -health, offset, height * health, ColorUtil.mixColors(Color.yellow, Color.red, health * 2), Color.red);
            	}
            	break;

            case "Standard":
            	final double bar = (pos.w - pos.y) * player.getHealth() / player.getMaxHealth();
            	Color color = health > 0.5 ? ColorUtil.mixColors(Color.GREEN, Color.YELLOW, (health - 0.5) * 2) : ColorUtil.mixColors(Color.YELLOW, Color.RED, health * 2);
            	RenderUtil.rectangle(pos.x - 3, pos.y, 1.5, pos.w - pos.y + 1.5, Color.BLACK);
            	RenderUtil.rectangle(pos.x - 2.5, pos.w - bar + offset, offset, bar + offset, color);
            	break;
            }       
        }
    };
}
