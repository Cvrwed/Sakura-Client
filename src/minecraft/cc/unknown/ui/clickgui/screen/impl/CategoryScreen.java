package cc.unknown.ui.clickgui.screen.impl;

import java.util.ArrayList;
import java.util.stream.Collectors;

import cc.unknown.Sakura;
import cc.unknown.module.api.Category;
import cc.unknown.ui.clickgui.ClickGui;
import cc.unknown.ui.clickgui.components.ModuleComponent;
import cc.unknown.ui.clickgui.screen.Screen;
import cc.unknown.util.Accessor;
import cc.unknown.util.gui.ScrollUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.util.vector.Vector2d;
import lombok.Getter;

@Getter
public final class CategoryScreen implements Screen, Accessor {

    private final StopWatch stopwatch = new StopWatch();

    public ScrollUtil scrollUtil = new ScrollUtil();
    public ArrayList<ModuleComponent> relevantModules;
    public Category category;
    private double endOfList, startOfList;

    @Override
    public void onRender(final int mouseX, final int mouseY, final float partialTicks) {
        if (this.category == null) return;

        final ClickGui clickGUI = this.getClickGUI();

        /* Scroll */
        scrollUtil.onRender();

        /* Draws modules in search */
        double positionY = clickGUI.position.y + 7 + scrollUtil.getScroll();
        startOfList = positionY;

        /* Draws all modules */
        double height = 0;

        for (final ModuleComponent module : this.relevantModules) {
            module.draw(new Vector2d(clickGUI.position.x + clickGUI.sidebar.sidebarWidth + 8, positionY), mouseX, mouseY, partialTicks);
            positionY += module.scale.y + 7;
            height += module.scale.y + 7;
        }

        endOfList = positionY;

        double padding = 7;
        double scrollX = clickGUI.getPosition().getX() + clickGUI.getScale().getX() - 4;
        double scrollY = clickGUI.getPosition().getY() + padding;

        scrollUtil.renderScrollBar(new Vector2d(scrollX, scrollY), getClickGUI().scale.y - padding * 2);

        scrollUtil.setMax(-height + clickGUI.scale.y - 7);
        stopwatch.reset();
    }

    @Override
    public void onKey(final char typedChar, final int keyCode) {
        for (final ModuleComponent module : this.getRelevantModules()) {
            module.key(typedChar, keyCode);
        }
    }

    @Override
    public void onClick(final int mouseX, final int mouseY, final int mouseButton) {
        if (relevantModules == null) return;

        for (final ModuleComponent moduleComponent : relevantModules) {
            moduleComponent.click(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void onMouseRelease() {
        if (this.category == null) return;

        for (final ModuleComponent module : this.getRelevantModules()) {
            module.released();
        }
    }

    /*@Override
    public void onBloom() {
        if (this.category == null) return;

        for (final ModuleComponent module : this.getRelevantModules()) {
            module.bloom();
        }
    }*/

    @Override
    public void onInit() {
        this.category = this.getCategory();
        if (this.category == null) return;

        this.relevantModules = Sakura.instance.getClickGui().getModuleList().stream()
                .filter((module) -> module.getModule().getModuleInfo().category() == this.category)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Category getCategory() {
        for (final Category category : Category.values()) {
            if (category.getClickGUIScreen() == getClickGUI().getSelectedScreen()) {
                return category;
            }
        }

        return null;
    }
}
