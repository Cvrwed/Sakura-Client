package cc.unknown.ui.clickgui.screen.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.stream.Collectors;

import cc.unknown.Sakura;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.api.Category;
import cc.unknown.ui.clickgui.ClickGui;
import cc.unknown.ui.clickgui.components.ModuleComponent;
import cc.unknown.ui.clickgui.screen.Colors;
import cc.unknown.ui.clickgui.screen.Screen;
import cc.unknown.ui.clickgui.screen.settings.Element;
import cc.unknown.util.Accessor;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.animation.Easing;
import cc.unknown.util.gui.ScrollUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.tuples.Triple;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.util.vector.Vector2f;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScriptScreen implements Screen, Accessor {
    public ScrollUtil scrollUtil = new ScrollUtil();
    public static int PADDING = 10;
    public static boolean SCROLL;

    private ArrayList<ModuleComponent> yourScripts = new ArrayList<>();

    private boolean registered;
    private Animation animation = new Animation(Easing.EASE_OUT_EXPO, 400);

    @Override
    public void onRender(final int mouseX, final int mouseY, final float partialTicks) {
        if (!registered) {
            Sakura.instance.getEventBus().register(this);
            registered = true;
        }

        ClickGui clickGUI = this.getClickGUI();
        Vector2f position = new Vector2f(getClickGUI().getPosition().x, getClickGUI().getPosition().y);
        Vector2f scale = new Vector2f(getClickGUI().getScale().x, getClickGUI().getScale().y);

        SCROLL = true;
        scrollUtil.onRender(SCROLL);

        position.x += clickGUI.sidebar.sidebarWidth + PADDING;
        position.y += scrollUtil.getScroll() + PADDING;
        scale.x += -PADDING * 2 - clickGUI.sidebar.sidebarWidth;
        
        String headerText = "Your Scripts";
        Fonts.MAIN.get(18, Weight.LIGHT).draw(headerText, position.x, position.y, Color.WHITE.getRGB());

        if (yourScripts.size() > 0) {
            String itemCountText = Integer.toString(yourScripts.size());
            Fonts.MAIN.get(18, Weight.LIGHT).draw(itemCountText, position.x + Fonts.MAIN.get(18, Weight.LIGHT).width(headerText) + PADDING / 2f, position.y, Sakura.instance.getThemeManager().getTheme().getAccentColor().getRGB());
        }
        
        position.y += PADDING + Fonts.MAIN.get(18, Weight.LIGHT).height();

        for (final ModuleComponent module : this.yourScripts) {
            module.draw(new Vector2d(clickGUI.position.x + clickGUI.sidebar.sidebarWidth + 8, position.y), mouseX, mouseY, partialTicks);
            position.y += module.scale.y + 7;
        }

        double padding = 7;
        double scrollX = clickGUI.getPosition().getX() + clickGUI.getScale().getX() - 4;
        double scrollY = clickGUI.getPosition().getY() + padding;

        scrollUtil.renderScrollBar(new Vector2d(scrollX, scrollY), getClickGUI().scale.y - padding * 2);

        scrollUtil.setMax(-(position.y - scrollUtil.getScroll() - clickGUI.position.y) + clickGUI.scale.y - 7);
    }

    @Override
    public void onKey(final char typedChar, final int keyCode) {
        for (final ModuleComponent module : yourScripts) {
            module.key(typedChar, keyCode);
        }
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton) {
        for (final ModuleComponent moduleComponent : yourScripts) {
            moduleComponent.click(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void onMouseRelease() {
        for (final ModuleComponent module : yourScripts) {
            module.released();
        }
    }

    @Override
    public void onInit() {
        if (!registered) {
            Sakura.instance.getEventBus().register(this);
            registered = true;
        };

        this.yourScripts = Sakura.instance.getClickGui().getModuleList().stream()
                .filter((module) -> module.getModule().getModuleInfo().category() == Category.SCRIPT)
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
