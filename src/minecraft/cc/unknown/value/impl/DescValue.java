package cc.unknown.value.impl;

import java.util.List;
import java.util.function.BooleanSupplier;

import cc.unknown.module.Module;
import cc.unknown.ui.clickgui.components.value.impl.DescValueComponent;
import cc.unknown.value.Mode;
import cc.unknown.value.Value;

public class DescValue extends Value<String> {

    public DescValue(final String name, final Module parent) {
        super(name, parent, null);
    }

    public DescValue(final String name, final Mode<?> parent) {
        super(name, parent, null);
    }

    public DescValue(final String name, final Module parent, final BooleanSupplier hideIf) {
        super(name, parent, null, hideIf);
    }

    public DescValue(final String name, final Mode<?> parent, final BooleanSupplier hideIf) {
        super(name, parent, null, hideIf);
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }

    @Override
    public DescValueComponent createUIComponent() {
        return new DescValueComponent(this);
    }
}