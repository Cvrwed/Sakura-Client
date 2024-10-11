package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.TargetComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

@ModuleInfo(aliases = "Tracers", description = "Renders a line from your crosshair to every player", category = Category.VISUALS)
public final class Tracers extends Module {

    @EventLink
    public final Listener<Render3DEvent> onRender3D = event -> {
        if (mc.gameSettings.hideGUI) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        mc.entityRenderer.orientCamera(mc.timer.renderPartialTicks);

        for (final Entity player : mc.world.playerEntities) {
            if (player == mc.player || player.isDead || Sakura.instance.getBotManager().contains(player)) {
                continue;
            }

            final double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
            final double y = (player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks()) + 1.62F;
            final double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

            final Color color = ColorUtil.withAlpha(
                    ColorUtil.mixColors(getTheme().getSecondColor(), getTheme().getFirstColor(), Math.min(1, mc.player.getDistanceToEntity(player) / 50)),
                    128);

            RenderUtil.drawLine(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY + mc.player.getEyeHeight(), mc.getRenderManager().renderPosZ, x, y, z, color, 1.5F);
        }

        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    };
}