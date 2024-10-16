package cc.unknown.module.impl.combat.aura.esp;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.impl.combat.KillAura;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.Mode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class RingESP extends Mode<KillAura> {

	public RingESP(String name, KillAura parent) {
		super(name, parent);
	}

    @EventLink
    public final Listener<Render3DEvent> onRender = event -> {
        if (this.getParent().espMode.is("Ring") && this.getParent().target != null) {
    		if (isClickGui()) return;

            final float partialTicks = mc.timer.renderPartialTicks;

            EntityLivingBase player = (EntityLivingBase) this.getParent().target;

            final Color color = getTheme().getSecondColor();

            if (mc.getRenderManager() == null || player == null) return;

            final double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks - (mc.getRenderManager()).renderPosX;
            final double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks + Math.sin(System.currentTimeMillis() / 2E+2) + 1 - (mc.getRenderManager()).renderPosY;
            final double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks - (mc.getRenderManager()).renderPosZ;

            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

            GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
            GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);

            GL11.glDepthMask(false);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GlStateManager.disableCull();

            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

            for (float i = 0; i <= Math.PI * 2 + ((Math.PI * 2) / 25); i += (float) ((Math.PI * 2) / 25)) {
                double vecX = x + 0.67 * Math.cos(i);
                double vecZ = z + 0.67 * Math.sin(i);

                RenderUtil.color(ColorUtil.withAlpha(color, (int) (255 * 0.25)));
                GL11.glVertex3d(vecX, y, vecZ);
            }

            for (float i = 0; i <= Math.PI * 2 + (Math.PI * 2) / 25; i += (Math.PI * 2) / 25) {
                double vecX = x + 0.67 * Math.cos(i);
                double vecZ = z + 0.67 * Math.sin(i);

                RenderUtil.color(ColorUtil.withAlpha(color, (int) (255 * 0.25)));
                GL11.glVertex3d(vecX, y, vecZ);

                RenderUtil.color(ColorUtil.withAlpha(color, 0));
                GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 200.0) / 2.0F, vecZ);
            }

            GL11.glEnd();

            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);

            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.enableCull();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            GL11.glPopMatrix();

            RenderUtil.color(getTheme().getFirstColor());
        }
    };
}
