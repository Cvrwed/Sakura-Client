package cc.unknown.ui.clickgui.components.value.impl;

import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.ui.clickgui.components.value.ValueComponent;
import cc.unknown.ui.clickgui.screen.Colors;
import cc.unknown.util.gui.GUIUtil;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.value.Mode;
import cc.unknown.value.Value;
import cc.unknown.value.impl.ListValue;
import cc.unknown.value.impl.ModeValue;

/**
 * @author Strikeless
 * @since 02.07.2022
 */
public class ListValueComponent extends ValueComponent {

    @Override
    public void draw(final Vector2d position, final int mouseX, final int mouseY, final float partialTicks) {
        final ListValue<?> listValue = (ListValue<?>) value;
        this.position = position;

        final String prefix = this.value.getName() + ":";

        Fonts.MAIN.get(16, Weight.LIGHT).draw(prefix, this.position.x, this.position.y, Colors.SECONDARY_TEXT.getRGBWithAlpha(opacity));
        Fonts.MAIN.get(16, Weight.LIGHT).draw(listValue instanceof ModeValue ? ((ModeValue) listValue).getValue().getName() : listValue.getValue().toString(), this.position.x + Fonts.MAIN.get(16, Weight.LIGHT).width(prefix) + 2, this.position.y, Colors.SECONDARY_TEXT.getRGBWithAlpha(opacity));
    }

    public ListValueComponent(final Value<?> value) {
        super(value);
    }

    @Override
    public boolean click(final int mouseX, final int mouseY, final int mouseButton) {
        if (this.position == null) {
            return false;
        }

        final ListValue<?> listValue = (ListValue<?>) value;

        final boolean left = mouseButton == 0;
        final boolean right = mouseButton == 1;

        if (GUIUtil.mouseOver(this.position.x, this.position.y - 3.5f, getClickGUI().width - 70, this.height, mouseX, mouseY)) {
            final int currentIndex = listValue.getModes().indexOf(listValue.getValue());

            Object value = null;
            if (left) {
                if (listValue.getModes().size() <= currentIndex + 1) {
                    value = listValue.getModes().get(0);
                } else {
                    value = listValue.getModes().get(currentIndex + 1);
                }
            } else if (right) {
                if (0 > currentIndex - 1) {
                    value = listValue.getModes().get(listValue.getModes().size() - 1);
                } else {
                    value = listValue.getModes().get(currentIndex - 1);
                }
            }

            if (value != null) {
                if (this.getValue() instanceof ModeValue) {
                    ((ModeValue)listValue).update((Mode<?>) value);
                } else {
                    listValue.setValueAsObject(value);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void released() {

    }

    @Override
    public void key(final char typedChar, final int keyCode) {

    }
}

