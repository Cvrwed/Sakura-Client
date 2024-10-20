package cc.unknown.util.render;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.unknown.util.Accessor;
import cc.unknown.util.interfaces.ThreadAccess;
import cc.unknown.util.time.StopWatch;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public class BackgroundUtil implements Accessor {
    private int pass = 0;
    private final StopWatch frames = new StopWatch();
    private final ResourceLocation[] images = new ResourceLocation[50];
    
    static {
    	for (int i = 1; i <= 50; i++) {
    		images[i - 1] = new ResourceLocation("sakura/images/background/" + i + ".jpg");
    	}
    }

    public static void renderBackground(GuiScreen gui) {
        if (frames.getElapsedTime() >= 50) {
            pass = (pass + 1) % 50;
            frames.reset();
        }

        RenderUtil.image(images[pass], 0, 0, gui.width, gui.height);
    }
    
	public void renderSplashScreen() {
		ScaledResolution sr = new ScaledResolution(mc);
		RenderUtil.image(new ResourceLocation("sakura/images/sakura.png"), 0, 0, sr.getScaledWidth(),
				sr.getScaledHeight());

	}
}