package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

@ModuleInfo(aliases = "Item ESP", description = "Renders all items", category = Category.VISUALS)
public final class ItemESP extends Module {

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		if (isClickGui()) return;
		
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        
        for (Entity e : mc.world.loadedEntityList) {
            if (e instanceof EntityItem) {
                EntityItem item = (EntityItem) e;
                float pTicks = mc.timer.renderPartialTicks;
                double RPX = mc.getRenderManager().renderPosX;
                double RPY = mc.getRenderManager().renderPosY;
                double RPZ = mc.getRenderManager().renderPosZ;
                double x = item.lastTickPosX + (item.posX - item.lastTickPosX) * pTicks - RPX;
                double y = item.lastTickPosY + (item.posY - item.lastTickPosY) * pTicks - RPY;
                double z = item.lastTickPosZ + (item.posZ - item.lastTickPosZ) * pTicks - RPZ;
                int r = getTheme().getAccentColor().getRed();
                int g = getTheme().getAccentColor().getGreen();
                int b = getTheme().getAccentColor().getBlue();
                int a = getTheme().getAccentColor().getAlpha();
                Color c = new Color(r, g, b, a);
                RenderUtil.renderBoxWithOutline(x, y - 0.7D, z, 0.5F, 0.5F, c);
            }
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
	};
}