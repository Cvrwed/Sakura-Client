package cc.unknown.value.impl;

import java.util.List;
import java.util.function.BooleanSupplier;

import cc.unknown.module.Module;
import cc.unknown.ui.clickgui.components.value.impl.StringValueComponent;
import cc.unknown.value.Mode;
import cc.unknown.value.Value;

public class StringValue extends Value<String> {

    public StringValue(final String name, final Module parent, final String defaultValue) {
        super(name, parent, defaultValue);
    }

    public StringValue(final String name, final Mode<?> parent, final String defaultValue) {
        super(name, parent, defaultValue);
    }

    public StringValue(final String name, final Module parent, final String defaultValue, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
    }

    public StringValue(final String name, final Mode<?> parent, final String defaultValue, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }

    @Override
    public StringValueComponent createUIComponent() {
        return new StringValueComponent(this);
    }
}