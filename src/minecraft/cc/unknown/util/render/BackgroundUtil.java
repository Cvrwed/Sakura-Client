package cc.unknown.util.render;

import org.lwjgl.opengl.GL11;

import cc.unknown.util.Accessor;
import cc.unknown.util.interfaces.ThreadAccess;
import cc.unknown.util.time.StopWatch;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class BackgroundUtil implements Accessor {
	private static int pass = 0;
	private static final int TOTAL_IMAGES = 50;
	private static StopWatch frames = new StopWatch();
	private static String imageName = "";

	public static void renderBackground(GuiScreen gui) {
	    if (frames.getElapsedTime() >= 60) {
	        for (int i = 1; i <= TOTAL_IMAGES; i++) {
	            if (pass % TOTAL_IMAGES == i - 1) {
	                imageName = "sakura/images/background/" + i + ".jpg";
	                break;
	            }
	        }

	        frames.reset();
	        pass++;
	    }

	    RenderUtil.image(new ResourceLocation(imageName), 0, 0, gui.width, gui.height);
	}

	public static void renderSplashScreen() {
		ScaledResolution sr = new ScaledResolution(mc);
		RenderUtil.image(new ResourceLocation("sakura/images/sakura.png"), 0, 0, sr.getScaledWidth(),
				sr.getScaledHeight());

	}
}