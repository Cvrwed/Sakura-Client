package cc.unknown.util.render;

import org.lwjgl.opengl.GL11;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

@UtilityClass
public class ScissorUtil {

    public void enable() {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public void disable() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public void scissor(ScaledResolution scaledResolution, double x, double y, double width, double height) {
        if (x + width == x || y + height == y || x < 0 || y + height < 0) return;
        final int scaleFactor = scaledResolution.getScaleFactor();
        GL11.glScissor((int) Math.round(x * scaleFactor), (int) Math.round((scaledResolution.getScaledHeight() - (y + height)) * scaleFactor), (int) Math.round(width * scaleFactor), (int) Math.round(height * scaleFactor));
    }
    
    public void scissor(double x, double y, double width, double height) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final double scale = sr.getScaleFactor();
        y = sr.getScaledHeight() - y;
        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;
        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }
}
