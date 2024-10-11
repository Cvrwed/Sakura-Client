package cc.unknown.ui.clickgui.components.theme;

import java.awt.Color;

import cc.unknown.ui.theme.Themes;
import cc.unknown.ui.theme.Themes.KeyColors;
import cc.unknown.util.Accessor;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.animation.Easing;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.vector.Vector3d;

public class ThemeKeyColorComponent implements Accessor {
    private final Themes.KeyColors color;

    private Vector3d lastDraw = new Vector3d(0, 0, 0);
    private final Animation dimAnimation = new Animation(Easing.EASE_OUT_QUINT, 500);
    private final Animation bloomAnimation = new Animation(Easing.EASE_OUT_QUINT, 500);

    public void draw(double x, double y, double width, boolean selected) {
        double value = dimAnimation.getValue();

        RenderUtil.roundedRectangle(x, y, width, 17, 5, new Color(18, 21, 30));
        RenderUtil.roundedRectangle(x + 0.5, y + 0.5, width - 1, 16, 4, color.getColor());

        RenderUtil.roundedRectangle(x, y, width, 17, 5, new Color(25, 25, 25,
                (int) ((1 - dimAnimation.getValue()) * 128)));

        this.lastDraw = new Vector3d(x, y, width);
    }

	public ThemeKeyColorComponent(KeyColors color) {
		this.color = color;
	}

	public Themes.KeyColors getColor() {
		return color;
	}

	public Vector3d getLastDraw() {
		return lastDraw;
	}

	public Animation getDimAnimation() {
		return dimAnimation;
	}

	public Animation getBloomAnimation() {
		return bloomAnimation;
	}

	public void setLastDraw(Vector3d lastDraw) {
		this.lastDraw = lastDraw;
	}
}