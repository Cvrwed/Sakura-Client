package cc.unknown.ui.menu.component.button;

import cc.unknown.ui.menu.component.MenuComponent;
import cc.unknown.util.MouseUtil;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.animation.Easing;
import lombok.Getter;

@Getter
public class MenuButton extends MenuComponent {

    private final Runnable runnable;
    public String name;
    private double size;
    
    private final Animation animation = new Animation(Easing.EASE_OUT_QUINT, 500);
    private final Animation hoverAnimation = new Animation(Easing.EASE_OUT_SINE, 250);

    public MenuButton(double x, double y, double width, double height, Runnable runnable) {
        super(x, y, width, height);
        this.runnable = runnable;
    }

    public MenuButton(double x, double y, double width, double height, Runnable runnable, String name) {
        super(x, y, width, height, runnable);
        this.name = name;
		this.runnable = runnable;
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        this.hoverAnimation.run(MouseUtil.isHovered(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY) ? 100 : 45);
    }

    public void runAction() {
        this.runnable.run();
    }
}
