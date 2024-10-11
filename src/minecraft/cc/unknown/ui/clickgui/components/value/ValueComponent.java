package cc.unknown.ui.clickgui.components.value;

import cc.unknown.util.Accessor;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ValueComponent implements Accessor {

    public double height = 14;
    public Vector2d position;
    public Value<?> value;
    public int opacity = 255;

    public ValueComponent(final Value<?> value) {
        this.value = value;
    }

    public abstract void draw(Vector2d position, int mouseX, int mouseY, float partialTicks);

    public abstract boolean click(int mouseX, int mouseY, int mouseButton);

    public abstract void released();

    public abstract void key(final char typedChar, final int keyCode);
}
