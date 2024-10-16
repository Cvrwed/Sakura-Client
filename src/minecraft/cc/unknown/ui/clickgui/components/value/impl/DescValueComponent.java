package cc.unknown.ui.clickgui.components.value.impl;

import java.awt.Color;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.ui.clickgui.components.value.ValueComponent;
import cc.unknown.ui.clickgui.screen.Colors;
import cc.unknown.util.gui.textbox.TextAlign;
import cc.unknown.util.gui.textbox.TextBox;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.value.Value;
import cc.unknown.value.impl.DescValue;
import cc.unknown.value.impl.StringValue;

public class DescValueComponent extends ValueComponent {

    public DescValueComponent(final Value<?> value) {
        super(value);

        final DescValue stringValue = (DescValue) value;
    }

    @Override
    public void draw(Vector2d position, int mouseX, int mouseY, float partialTicks) {
        this.position = position;
        this.height = 14;

        Fonts.MAIN.get(14, Weight.BOLD).draw(this.value.getName(), this.position.x, this.position.y, Colors.SECONDARY_TEXT.getRGBWithAlpha(opacity));
    }

    @Override
    public boolean click(final int mouseX, final int mouseY, final int mouseButton) {
        return false;
    }

    @Override
    public void released() {
    }

    @Override
    public void key(final char typedChar, final int keyCode) {

    }
}
