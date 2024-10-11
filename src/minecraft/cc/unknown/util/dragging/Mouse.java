package cc.unknown.util.dragging;

import cc.unknown.util.Accessor;
import cc.unknown.util.vector.Vector2d;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.ScaledResolution;

@UtilityClass
public class Mouse implements Accessor {
    public Vector2d getMouse() {
        final ScaledResolution scaledResolution = mc.scaledResolution;
        final int mouseX = org.lwjgl.input.Mouse.getX() * scaledResolution.getScaledWidth() / mc.displayWidth;
        final int mouseY = scaledResolution.getScaledHeight() - org.lwjgl.input.Mouse.getY() * scaledResolution.getScaledHeight() / mc.displayHeight - 1;
        return new Vector2d(mouseX, mouseY);
    }
}
