package cc.unknown.ui.menu.account.display;

import java.awt.Color;
import java.text.DateFormat;
import java.util.Date;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.ui.menu.MenuColors;
import cc.unknown.ui.menu.account.impl.RenameAccountScreen;
import cc.unknown.ui.menu.component.button.impl.MenuLabelButton;
import cc.unknown.util.Accessor;
import cc.unknown.util.MouseUtil;
import cc.unknown.util.SkinUtil;
import cc.unknown.util.account.Account;
import cc.unknown.util.account.impl.MicrosoftAccount;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.animation.Easing;
import cc.unknown.util.font.Font;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.StencilUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountViewModel<T extends Account> implements Accessor, MenuColors {
    private static final Font FONT_RENDERER = Fonts.MAIN.get(24, Weight.LIGHT);
    private static final Font INFO_FONT_RENDERER = Fonts.MAIN.get(18, Weight.LIGHT);
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.SHORT);
    private static final Color BLOOM_COLOR = ColorUtil.withAlpha(Color.BLACK, 135);
    private static final Color FONT_COLOR = ColorUtil.withAlpha(Color.WHITE, 255);
    private static final Color INFO_COLOR = ColorUtil.withAlpha(FONT_COLOR.darker(), 255);
    private static final Color BACKGROUND_COLOR = ColorUtil.withAlpha(BUTTON, 30);
    private static final Color BORDER_ONE_COLOR = ColorUtil.withAlpha(BORDER_TWO, 255);
    private static final Color BORDER_TWO_COLOR = ColorUtil.withAlpha(BORDER_ONE, 255);

    private final Animation hoverAnimation;
    private final Animation positionAnimation;
    private T account;
    private float x;
    private float y;
    private double scroll;
    private float width;
    private float height;
    private int screenHeight;
    private boolean removable;
    private MenuLabelButton[] labelButtons;

    private final Runnable defaultRenderRunnable = () -> {
        RenderUtil.roundedRectangle(x, y + scroll, width, height, 5, BACKGROUND_COLOR);
        renderHead(x + 4, y + 4 + scroll, 32);

        FONT_RENDERER.draw(account.getName(), x + 40, y + 6+  scroll, FONT_COLOR.getRGB());
        INFO_FONT_RENDERER.draw("Last login: " + DATE_FORMAT.format(new Date(account.getLastUsed())), x + 40, y + 19+ scroll, INFO_COLOR.getRGB());
        INFO_FONT_RENDERER.draw("Actions:", x + 40, y + 29 + scroll, INFO_COLOR.getRGB());

        for (MenuLabelButton button : labelButtons) {
            button.setY(button.getY() + scroll);
            button.draw(0, 0, 0);
            button.setY(button.getY() - scroll);
        }
    };

    private final Runnable invalidRenderRunnable = () -> {
        RenderUtil.roundedRectangle(x, y, width, height, 5, BACKGROUND_COLOR);
        renderInvalidHead(x + 4, y + 4, 32);

        FONT_RENDERER.draw("Waiting...", x + 40, y + 6, FONT_COLOR.getRGB());
        INFO_FONT_RENDERER.draw("Last login: -", x + 40, y + 19, INFO_COLOR.getRGB());
        INFO_FONT_RENDERER.draw("Actions: -", x + 40, y + 29, INFO_COLOR.getRGB());
    };

    private final Runnable bloomRunnable = () -> RenderUtil.roundedRectangle(x + 0.5F, y + 0.5F + scroll, width - 1, height - 1, 6, BLOOM_COLOR);

    public AccountViewModel(T account, float x, float y, float width, float height) {
        this.account = account;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hoverAnimation = new Animation(Easing.EASE_OUT_CUBIC, 200);
        this.positionAnimation = new Animation(Easing.EASE_OUT_CUBIC, 200);

        if (account instanceof MicrosoftAccount) {
            this.labelButtons = new MenuLabelButton[]{
                    new MenuLabelButton(x + 76, y + height - 12, 24, 8, () -> removable = true, "Delete", Color.RED)
            };
        } else {
            int labelPadding = 2;
            this.labelButtons = new MenuLabelButton[]{
                    new MenuLabelButton(x + 76, y + height - 12, 28, 8, () -> mc.displayGuiScreen(new RenameAccountScreen(this)), "Rename", Color.YELLOW),
                    new MenuLabelButton(x + 76 + 28 + labelPadding, y + height - 12, 24, 8, () -> removable = true, "Delete", Color.RED)
            };
        }
    }

    /**
     * Draws the account display.
     *
     * @return If the account display can be drawn.
     */
    public boolean draw() {
        if (isOutOfScreen()) {
            return false;
        }

        // Basic rendering is done here.
        // Override this method to add custom rendering.
        bloomRunnable.run();

        if (account.isValid()) {
            
        	defaultRenderRunnable.run();
        } else {
        	invalidRenderRunnable.run();
        }
        return true;
    }

    /**
     * Checks if a mouse click is inside the account display.
     *
     * @param mouseX      The mouse's x position.
     * @param mouseY      The mouse's y position.
     * @param mouseButton The mouse button that was clicked.
     * @return If the mouse click was inside the account display.
     */
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0) {
            return false;
        }

        // First if in bounds to not check unnecessary things.
        if (MouseUtil.isHovered(this.x, this.y + scroll, this.width, this.height, mouseX, mouseY)) {
            if (account.isValid()) {
                for (MenuLabelButton button : labelButtons) {
                    if (MouseUtil.isHovered(button.getX(), button.getY() + scroll, button.getWidth(), button.getHeight(), mouseX, mouseY)) {
                        button.runAction();
                        return true;
                    }
                }
            }

            // Then login if no inner button press.
            return account.login();
        }
        return false;
    }

    private void renderHead(final double x, final double y, final int size) {
        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        RenderUtil.roundedRectangle(x, y, size, size, 5, this.getTheme().getBackgroundShade());
        StencilUtil.bindReadStencilBuffer(1);
        RenderUtil.image(SkinUtil.getResourceLocation(SkinUtil.SkinType.SKIN, account.getUuid(), 24), x, y, size, size, ColorUtil.withAlpha(Color.WHITE, (int) (200 + this.hoverAnimation.getValue())));
        StencilUtil.uninitStencilBuffer();
    }

    private void renderInvalidHead(final double x, final double y, final int size) {
        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        RenderUtil.roundedRectangle(x, y, size, size, 5, this.getTheme().getBackgroundShade());
        StencilUtil.bindReadStencilBuffer(1);

        double brightness = Math.sin(System.currentTimeMillis() * 0.003) * -0.5 + 0.5;
        Color color = Color.getHSBColor(1.0F, 0.0F, (float) (brightness * 0.25) + 0.5F);
        RenderUtil.image(SkinUtil.getResourceLocation(SkinUtil.SkinType.SKIN, account.getUuid(), 24), x, y, size, size, color);
        StencilUtil.uninitStencilBuffer();
    }

    /**
     * Updates the account display.
     *
     * @return If the account display is valid.
     */
    public boolean update() {
        if (!this.account.isValid()) {
            return false;
        }

        positionAnimation.run(this.y);
        return true;
    }

    private void addScrollOffset(float offset) {
        this.y += offset;
    }

    /**
     * Checks if the account display should be drawn.
     *
     * @return Allowed to draw.
     */
    private boolean isOutOfScreen() {
        return this.y + this.height < 0 || this.y > screenHeight;
    }

    public boolean isRemovable() {
        return removable || !account.isValid();
    }
}
