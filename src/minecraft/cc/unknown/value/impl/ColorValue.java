package cc.unknown.value.impl;

import java.awt.Color;
import java.util.List;
import java.util.function.BooleanSupplier;

import cc.unknown.module.Module;
import cc.unknown.ui.clickgui.components.value.impl.ColorValueComponent;
import cc.unknown.value.Mode;
import cc.unknown.value.Value;

/**
 * @author Alan
 * @since 10/19/2022
 */
public class ColorValue extends Value<Color> {

    public ColorValue(final String name, final Module parent, final Color defaultValue) {
        super(name, parent, defaultValue);
    }

    public ColorValue(final String name, final Mode<?> parent, final Color defaultValue) {
        super(name, parent, defaultValue);
    }

    public ColorValue(final String name, final Module parent, final Color defaultValue, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
    }

    public ColorValue(final String name, final Mode<?> parent, final Color defaultValue, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }

    @Override
    public ColorValueComponent createUIComponent() {
        return new ColorValueComponent(this);
    }
}