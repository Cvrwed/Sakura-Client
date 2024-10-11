package cc.unknown.ui.clickgui.components.category;

import static cc.unknown.util.animation.Easing.LINEAR;

import java.awt.Color;

import cc.unknown.Sakura;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.api.Category;
import cc.unknown.ui.clickgui.ClickGui;
import cc.unknown.ui.clickgui.screen.Screen;
import cc.unknown.util.Accessor;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.gui.GUIUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.vector.Vector2d;
import net.minecraft.client.renderer.GlStateManager;

public final class CategoryComponent implements Accessor {

    private final Animation animation = new Animation(LINEAR, 500);
    public final Category category;
    private long lastTime = 0;
    private double selectorOpacity;

    private float x, y;
    private boolean down;

    public CategoryComponent(final Category category) {
        this.category = category;
    }

    public void render(final double offset, final double sidebarWidth, final double opacity, final Screen selectedScreen) {
        final ClickGui clickGUI = Sakura.instance.getClickGui();

        if (System.currentTimeMillis() - lastTime > 300) lastTime = System.currentTimeMillis();
        final long time = System.currentTimeMillis();

        /* Gets position depending on sidebar animation */
        x = (float) (clickGUI.position.x - (69 - sidebarWidth) - 21);
        y = (float) (clickGUI.position.y + offset) + 5;

        /* Animations */
        animation.setDuration(200);
        animation.run(selectedScreen.equals(category.getClickGUIScreen()) ? 255 : 0);

        final double spacer = 4;
        final double width = Fonts.MAIN.get(16, Weight.LIGHT).width(category.getName()) + spacer * 2;

        double scale = 0.5;
        GlStateManager.pushMatrix();

        RenderUtil.roundedRectangle(x, y - 5.5, width + 8, 15, 5,
                ColorUtil.withAlpha(getTheme().getAccentColor(new Vector2d(0, y / 5D)), (int) (Math.min(animation.getValue(), opacity))).darker());

        int color = new Color(255, 255, 255, Math.min(selectedScreen.equals(category.getClickGUIScreen()) ? 255 : 200, (int) opacity)).hashCode();


        Fonts.MAIN.get(16, Weight.LIGHT).draw(category.getName(), (float) (x + animation.getValue() / 80f + 3 + spacer), y, color);

        GlStateManager.popMatrix();

        lastTime = time;
    }

    public void click(final float mouseX, final float mouseY, final int button) {
        final boolean left = button == 0;
        if (GUIUtil.mouseOver(x - 11, y - 5, 70, 22, mouseX, mouseY) && left) {
            this.getClickGUI().switchScreen(this.category);
            down = true;
        }
    }

    public void release() {
        down = false;
    }
}