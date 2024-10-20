package cc.unknown.ui.clickgui.screen.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;

import cc.unknown.Sakura;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.ui.clickgui.ClickGui;
import cc.unknown.ui.clickgui.screen.Screen;
import cc.unknown.ui.clickgui.screen.settings.Element;
import cc.unknown.ui.clickgui.screen.settings.Row;
import cc.unknown.util.Accessor;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.animation.Easing;
import cc.unknown.util.gui.ScrollUtil;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.util.vector.Vector2f;
import lombok.Getter;

@Getter
public final class ConfigsScreen implements Screen, Accessor {

    public ScrollUtil scrollUtil = new ScrollUtil();
    public static int PADDING = 10;
    public static boolean SCROLL;

    private Row yourConfigs = new Row(Arrays.asList(
            new Element("", ""),
            new Element("", ""),
            new Element("", "")
    ), "Your Configs");

    public Row[] rows = new Row[]{yourConfigs};
    private boolean registered;
    private Animation animation = new Animation(Easing.EASE_OUT_EXPO, 400);
    private static final int MAX_ITEMS_PER_ROW = 3;

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
        for (Row row : rows) row.onScroll();
        scrollUtil.onRender(SCROLL);

        position.x += clickGUI.sidebar.sidebarWidth + PADDING;
        position.y += scrollUtil.getScroll() + PADDING;
        scale.x += -PADDING * 2 - clickGUI.sidebar.sidebarWidth;
        
        String headerText = yourConfigs.getName();
        Fonts.MAIN.get(18, Weight.LIGHT).draw(headerText, position.x, position.y, Color.WHITE.getRGB());

        if (yourConfigs.size() > 0) {
            String itemCountText = Integer.toString(yourConfigs.size());
            Fonts.MAIN.get(18, Weight.LIGHT).draw(itemCountText, position.x + Fonts.MAIN.get(18, Weight.LIGHT).width(headerText) + PADDING / 2f, position.y, Sakura.instance.getThemeManager().getTheme().getAccentColor().getRGB());
        }

        position.y += Fonts.MAIN.get(18, Weight.LIGHT).height() + PADDING;

        double currentX = position.x;
        double currentY = position.y;
        int itemCount = 0;

        for (Row row : rows) {
            currentX = position.x;
            currentY = position.y;

            if (!row.isEmpty()) {
	            for (int i = 0; i < row.size(); i++) {
	                Element element = row.get(i);
	
	                if (element != null && !element.getTitle().isEmpty()) {
	                    element.render(new Vector2f((float) currentX, (float) currentY));
	
	                    currentX += element.getScale().x + PADDING;
	                    itemCount++;
	
	                    if (itemCount % MAX_ITEMS_PER_ROW == 0) {
	                        currentX = position.x;
	                        currentY += (yourConfigs.get(0).getScale().y + PADDING);
	                    }
	                }
	            }
	
	            currentY += (yourConfigs.get(0).getScale().y + PADDING);
	        }
        }

        double padding = 7;
        double scrollX = clickGUI.getPosition().getX() + clickGUI.getScale().getX() - 4;
        double scrollY = clickGUI.getPosition().getY() + padding;

        scrollUtil.renderScrollBar(new Vector2d(scrollX, scrollY), getClickGUI().scale.y - padding * 2);

        scrollUtil.setMax(-(currentY - scrollUtil.getScroll() - clickGUI.position.y) + clickGUI.scale.y - 7);
    }

    @Override
    public void onKey(final char typedChar, final int keyCode) {
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton) {
        for (Row row : rows) {
            Iterator<Element> iterator = row.iterator();
            while (iterator.hasNext()) {
                Element element = iterator.next();
                if (element != null) {
                    element.onClick(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void onMouseRelease() {
    }

    @Override
    public void onInit() {
        if (!registered) {
            Sakura.instance.getEventBus().register(this);
            registered = true;
        }

        for (Row row : rows) {
            row.init();
        }

        Sakura.instance.getConfigManager().update();
        yourConfigs.clear();
        Sakura.instance.getConfigManager().forEach(config ->
                yourConfigs.add(
                		new Element("Click to load", config.getFile().getName(), config::read)));
    }
}