package cc.unknown.ui.menu.component.button.impl;

import java.awt.Color;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.ui.menu.component.button.MenuButton;
import cc.unknown.util.MouseUtil;
import cc.unknown.util.font.Font;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.util.ResourceLocation;

public class MenuAccountTypeButton extends MenuButton {
    private static final Font FONT_RENDERER = Fonts.MINECRAFT.get(24, Weight.BOLD);
    private final ResourceLocation resourceLocation;

    public MenuAccountTypeButton(double x, double y, double width, double height, Runnable runnable, String name, ResourceLocation resourceLocation) {
        super(x, y, width, height, runnable, name);
        this.resourceLocation = resourceLocation;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        // Runs the animation update - keep this
        this.getHoverAnimation().run(MouseUtil.isHovered(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY) ? 130 : 45);

        // Colors for rendering
        final double value = getY();
        final Color fontColor = ColorUtil.withAlpha(Color.WHITE, (int) (150 + this.getHoverAnimation().getValue()));

        // Renders the button text
        RenderUtil.roundedRectangle(this.getX(), value, this.getWidth(), this.getHeight(), 5, ColorUtil.withAlpha(Color.BLACK, 130));
                
        int imageSize = 64;
        RenderUtil.image(resourceLocation, this.getX() + this.getWidth() / 2.0F - imageSize / 2, value + this.getHeight() / 2.0F - imageSize / 2, imageSize, imageSize, fontColor);

        FONT_RENDERER.drawCentered(this.name, (float) (this.getX() + this.getWidth() / 2.0F), (float) (value + this.getHeight() / 2.0F - imageSize / 2 - 24), fontColor.getRGB());
        
    }
}
