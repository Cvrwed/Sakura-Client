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
    private static final StopWatch frames = new StopWatch();
    private static String imageName = "";
    private static ResourceLocation[] images = new ResourceLocation[TOTAL_IMAGES];

    static {
        for (int i = 1; i <= TOTAL_IMAGES; i++) {
            images[i - 1] = new ResourceLocation("sakura/images/background/" + i + ".jpg");
        }
    }

    public static synchronized void renderBackground(GuiScreen gui) {
        if (frames.getElapsedTime() >= 50) {
            int index = pass % TOTAL_IMAGES;
            imageName = images[index].toString();

            frames.reset();
            pass++;
        }

        RenderUtil.image(images[pass % TOTAL_IMAGES], 0, 0, gui.width, gui.height);
    }

	public static void renderSplashScreen() {
		ScaledResolution sr = new ScaledResolution(mc);
		RenderUtil.image(new ResourceLocation("sakura/images/sakura.png"), 0, 0, sr.getScaledWidth(),
				sr.getScaledHeight());

	}
}