package cc.unknown.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "Chams", description = "Renders the player through blocks or walls", category = Category.VISUALS)
public final class Chams extends Module {
    
    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
		if (isClickGui()) return;

		for (EntityPlayer player : mc.world.playerEntities) {
			if (player == mc.player || player.isDead) {
				continue;
			}
			
			final float partialTicks = mc.timer.renderPartialTicks;
			final double renderPosX = mc.getRenderManager().renderPosX;
			final double renderPosY = mc.getRenderManager().renderPosY;
			final double renderPosZ = mc.getRenderManager().renderPosZ;
			final Render<EntityPlayer> render = mc.getRenderManager().getEntityRenderObject(player);
			if (render == null) continue;
	
			Color color = new Color(0);
			if (color.getAlpha() <= 0) continue;
	
			double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks - renderPosX;
			double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks - renderPosY;
			double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks - renderPosZ;
			float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
	

			GL11.glColor4f(1.0f, 1.0f, 1.0f, color.getAlpha());

			render.doRender(player, x, y, z, yaw, partialTicks);
	
			player.hideNameTag();
			player.hide();
		}
	
		RenderHelper.disableStandardItemLighting();
		mc.entityRenderer.disableLightmap();   
    };
}
