package cc.unknown.ui.clickgui.screen.settings;

import java.awt.Color;
import java.util.ArrayList;

import cc.unknown.Sakura;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.ui.clickgui.ClickGui;
import cc.unknown.ui.clickgui.components.ModuleComponent;
import cc.unknown.ui.clickgui.components.value.ValueComponent;
import cc.unknown.ui.clickgui.components.value.impl.StringValueComponent;
import cc.unknown.ui.clickgui.screen.Colors;
import cc.unknown.ui.clickgui.screen.Screen;
import cc.unknown.util.Accessor;
import cc.unknown.util.gui.ScrollUtil;
import cc.unknown.util.gui.textbox.TextAlign;
import cc.unknown.util.gui.textbox.TextBox;
import cc.unknown.util.time.StopWatch;
import cc.unknown.util.vector.Vector2d;
import lombok.Getter;

@Getter
public final class ConfigScreen implements Screen, Accessor {

    public final TextBox searchBar = new TextBox(new Vector2d(200, 200), Fonts.MAIN.get(20, Weight.LIGHT), Color.WHITE, TextAlign.CENTER, "Start typing to search...", 150);
    private final StopWatch stopwatch = new StopWatch();

    public ScrollUtil scrollUtil = new ScrollUtil();
    public ArrayList<ModuleComponent> relevantModules = new ArrayList<>();
    private double opacity = 255;
    private double endOfList, startOfList;

    private boolean typedWhileOpen;

    @Override
    public void onRender(final int mouseX, final int mouseY, final float partialTicks) {
        final ClickGui clickGUI = this.getClickGUI();

        /* Setting searchbar color to clickgui fontcolor */
        if (scrollUtil.getTarget() < 0) {
            opacity -= stopwatch.getElapsedTime() * 4;
        } else {
            opacity += stopwatch.getElapsedTime() * 4;
        }
        opacity = Math.min(Math.max(0, opacity), 255);
        searchBar.setColor(Colors.TEXT.getWithAlpha((int) opacity));

        /* Setting position of searchbar */
        final Vector2d positionOfSearch = new Vector2d(((clickGUI.position.x + clickGUI.sidebar.sidebarWidth) +
                (clickGUI.scale.x - clickGUI.sidebar.sidebarWidth) / 2), (float) (clickGUI.position.y + 17 + scrollUtil.getScroll()));

        searchBar.setPosition(positionOfSearch);

        /* Draws searchbar */
        searchBar.draw();

        /* Scroll */
        scrollUtil.onRender();

        /* Draws modules in search */
        double positionY = clickGUI.position.y + 35 + scrollUtil.getScroll();
        startOfList = positionY;

        /* Draws all modules */
        double height = 0;
        for (final ModuleComponent module : this.relevantModules) {
            module.draw(new Vector2d(clickGUI.position.x + clickGUI.sidebar.sidebarWidth + 4, positionY), mouseX, mouseY, partialTicks);
            positionY += module.scale.y + 5;
            height += module.scale.y + 5;
        }

        endOfList = positionY;

        scrollUtil.setMax(-height + clickGUI.scale.y - 37);

        stopwatch.reset();
    }

    @Override
    public void onKey(final char typedChar, final int keyCode) {
        if (!typedWhileOpen && "abcdefghijklmnopqrstuvwxyz1234567890 ".contains(String.valueOf(typedChar).toLowerCase())) {
            typedWhileOpen = true;
            setSearchBarText(" ");
        }

        if (!this.activeTextBox()) {
            searchBar.setSelected(true);
        }

        searchBar.key(typedChar, keyCode);
        scrollUtil.setTarget(0);

        relevantModules = getRelevantModules(searchBar.getText());

        for (final ModuleComponent module : this.getRelevantModules()) {
            module.key(typedChar, keyCode);
        }
    }

    @Override
    public void onClick(final int mouseX, final int mouseY, final int mouseButton) {
        for (final ModuleComponent moduleComponent : relevantModules) {
            moduleComponent.click(mouseX, mouseY, mouseButton);
        }

        searchBar.click(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onMouseRelease() {
        for (final ModuleComponent module : this.getRelevantModules()) {
            module.released();
        }
    }

    /*@Override
    public void onBloom() {
        for (final ModuleComponent module : this.getRelevantModules()) {
            module.bloom();
        }
    }*/

    @Override
    public void onInit() {
        relevantModules = getRelevantModules(searchBar.getText());

        typedWhileOpen = false;
    }

    private boolean isInView(final double position) {
        return (position > getClickGUI().position.y && position < getClickGUI().position.y + getClickGUI().scale.y);
    }

    public ArrayList<ModuleComponent> getRelevantModules(final String search) {
        final ArrayList<ModuleComponent> relevantModules = new ArrayList<>();

        for (final ModuleComponent module : Sakura.instance.getClickGui().getModuleList()) {
            for (String alias: module.getModule().getModuleInfo().aliases()) {
                if (alias.toLowerCase().replaceAll(" ", "")
                        .contains(search.toLowerCase().replaceAll(" ", ""))){
                    relevantModules.add(module);
                    break;
                }
            }
        }

        return relevantModules;
    }

    public void setSearchBarText(final String text) {
        this.searchBar.setText(text);
        relevantModules = getRelevantModules(searchBar.getText());
    }

    public boolean activeTextBox() {
        for (final ModuleComponent moduleComponent : relevantModules) {
            for (final ValueComponent value : moduleComponent.getValueList()) {
                if (value instanceof StringValueComponent && ((StringValueComponent) value).textBox.selected) {
                    return true;
                }
            }
        }

        return false;
    }
}
