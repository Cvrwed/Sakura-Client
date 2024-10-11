package cc.unknown.ui.clickgui.components.value.impl;

import cc.unknown.ui.clickgui.components.value.ValueComponent;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.value.Value;
import net.minecraft.util.ResourceLocation;

public class PositionValueComponent extends ValueComponent {

    public PositionValueComponent(final Value<?> value) {
        super(value);
    }

    @Override
    public void draw(final Vector2d position, final int mouseX, final int mouseY, final float partialTicks) {
        this.height = 0;
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
