package cc.unknown.util;

import org.lwjgl.input.Mouse;

import cc.unknown.util.vector.Vector2d;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MouseUtil implements Accessor {
    public boolean isHovered(final double x, final double y, final double width, final double height, final int mouseX, final int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public boolean isHovered(final double x, final double y, final double width, final double height) {
        Vector2d mouse = mouse();
        return mouse.x >= x && mouse.x < x + width && mouse.y >= y && mouse.y < y + height;
    }

    public Vector2d mouse() {
        final int i1 = mc.scaledResolution.getScaledWidth();
        final int j1 = mc.scaledResolution.getScaledHeight();
        final int mouseX = Mouse.getX() * i1 / mc.displayWidth;
        final int mouseY = j1 - Mouse.getY() * j1 / mc.displayHeight - 1;

        return new Vector2d(mouseX, mouseY);
    }
}
