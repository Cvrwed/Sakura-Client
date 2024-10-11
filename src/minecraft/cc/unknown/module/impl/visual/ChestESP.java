package cc.unknown.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

@ModuleInfo(aliases = "Chest ESP", description = "Renders all chests", category = Category.VISUALS)
public final class ChestESP extends Module {

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		final Runnable runnable = () -> {
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glPushMatrix();

			int amount = 0;
			for (final TileEntity tileEntity : mc.world.loadedTileEntityList) {
				if (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest) {
					GL11.glPushMatrix();

					final RenderManager renderManager = mc.getRenderManager();

					final double x = (tileEntity.getPos().getX() + 0.5) - renderManager.renderPosX;
					final double y = tileEntity.getPos().getY() - renderManager.renderPosY;
					final double z = (tileEntity.getPos().getZ() + 0.5) - renderManager.renderPosZ;

					GL11.glTranslated(x, y, z);

					GL11.glRotated(-renderManager.playerViewY, 0.0D, 1.0D, 0.0D);
					GL11.glRotated(renderManager.playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0D : 1.0D, 0.0D, 0.0D);

					final float scale = 1 / 100f;
					GL11.glScalef(-scale, -scale, scale);

					final Color c = getTheme().getAccentColor();

					final float offset = renderManager.playerViewX * 0.5f;

					RenderUtil.lineNoGl(-50, offset, 50, offset, c);
					RenderUtil.lineNoGl(-50, -95 + offset, -50, offset, c);
					RenderUtil.lineNoGl(-50, -95 + offset, 50, -95 + offset, c);
					RenderUtil.lineNoGl(50, -95 + offset, 50, offset, c);

					GL11.glPopMatrix();
					amount++;
				}
			}

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		};

		runnable.run();
	};
}