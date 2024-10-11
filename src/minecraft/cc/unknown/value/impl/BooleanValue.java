package cc.unknown.value.impl;

import java.util.List;
import java.util.function.BooleanSupplier;

import cc.unknown.module.Module;
import cc.unknown.ui.clickgui.components.value.impl.BooleanValueComponent;
import cc.unknown.value.Mode;
import cc.unknown.value.Value;

public class BooleanValue extends Value<Boolean> {

    private final Mode<?> mode;

    public BooleanValue(final String name, final Module parent, final Boolean defaultValue) {
        super(name, parent, defaultValue);
        this.mode = null;
    }

    public BooleanValue(final String name, final Mode<?> parent, final Boolean defaultValue) {
        super(name, parent, defaultValue);
        this.mode = null;
    }

    public BooleanValue(final String name, final Module parent, final Boolean defaultValue, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
        this.mode = null;
    }

    public BooleanValue(final String name, final Mode<?> parent, final Boolean defaultValue, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
        this.mode = null;
    }

    public BooleanValue(final String name, final Module parent, final Boolean defaultValue, final Mode<?> mode) {
        super(name, parent, defaultValue);
        this.mode = mode;
        getMode().getValues().forEach(value -> value.setHideIf(() -> !getValue()));
    }

    public BooleanValue(final String name, final Mode<?> parent, final Boolean defaultValue, final Mode<?> mode) {
        super(name, parent, defaultValue);
        this.mode = mode;
    }

    public BooleanValue(final String name, final Module parent, final Boolean defaultValue, final BooleanSupplier hideIf, final Mode<?> mode) {
        super(name, parent, defaultValue, hideIf);
        this.mode = mode;
    }

    public BooleanValue(final String name, final Mode<?> parent, final Boolean defaultValue, final BooleanSupplier hideIf, final Mode<?> mode) {
        super(name, parent, defaultValue, hideIf);
        this.mode = mode;
    }

	@Override
    public void setValue(final Boolean value) {
        super.setValue(value);

        if (this.mode != null && this.getParent() != null) {
            if (((Module) this.getParent()).isEnabled()) {
                if (this.getValue()) {
                    this.mode.register();
                } else {
                    this.mode.unregister();
                }
            }
        }
    }

    @Override
    public List<Value<?>> getSubValues() {
        if (getMode() == null) return null;
        else return getMode().getValues();
    }

    @Override
    public void setValueAsObject(final Object value) {
        super.setValueAsObject(value);

        if (this.mode != null && this.getParent() != null) {
            if (((Module) this.getParent()).isEnabled() && this.getValue()) {
                if (this.getValue()) this.mode.onEnable();
                else this.mode.onDisable();
            }
        }
    }

    @Override
    public BooleanValueComponent createUIComponent() {
        return new BooleanValueComponent(this);
    }

	public Mode<?> getMode() {
		return mode;
	}
}