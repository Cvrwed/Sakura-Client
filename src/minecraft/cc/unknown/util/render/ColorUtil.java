package cc.unknown.util.render;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import cc.unknown.util.math.MathUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

@UtilityClass
public final class ColorUtil {

    public void glColor(final int hex) {
        final float a = (hex >> 24 & 0xFF) / 255.0F;
        final float r = (hex >> 16 & 0xFF) / 255.0F;
        final float g = (hex >> 8 & 0xFF) / 255.0F;
        final float b = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, a);
    }

    public void glColor(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
    }

    public  Color withAlpha(final Color color, final int alpha) {
        if (alpha == color.getAlpha()) return color;
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) MathUtil.clamp(0, 255, alpha));
    }

    public Color mixColors(final Color color1, final Color color2, final double percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }
    
	public Color getAlphaColor(Color color, int alpha) {
	    int clampedAlpha = MathHelper.clamp_int(alpha, 0, 255);
	    if (color.getAlpha() == clampedAlpha) {
	        return color;
	    }
	    return new Color(color.getRed(), color.getGreen(), color.getBlue(), clampedAlpha);
	}

	public void setColor(Color color) {
	    float red = color.getRed() / 255f;
	    float green = color.getGreen() / 255f;
	    float blue = color.getBlue() / 255f;
	    float alpha = color.getAlpha() / 255f;
	    GlStateManager.color(red, green, blue, alpha);
	}
	
    public Color averageColor(BufferedImage bi, int width, int height, int pixelStep) {

        int[] color = new int[3];
        for (int x = 0; x < width; x += pixelStep) {
            for (int y = 0; y < height; y += pixelStep) {
                Color pixel = new Color(bi.getRGB(x, y));
                color[0] += pixel.getRed();
                color[1] += pixel.getGreen();
                color[2] += pixel.getBlue();
            }
        }
        int num = width * height;
        return new Color(color[0] / num, color[1] / num, color[2] / num);
    }
}
