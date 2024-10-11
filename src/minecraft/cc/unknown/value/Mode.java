package cc.unknown.value;

import java.util.ArrayList;
import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.util.Accessor;
import cc.unknown.util.interfaces.ThreadAccess;
import cc.unknown.util.interfaces.Toggleable;

public abstract class Mode<T> implements Accessor, Toggleable, ThreadAccess {
    private final String name;
    private final T parent;
    private final List<Value<?>> values = new ArrayList<>();

    public Mode(String name, T parent) {
		this.name = name;
		this.parent = parent;
	}

	public void register() {
        Sakura.instance.getEventBus().register(this);
        this.onEnable();
    }

    public void unregister() {
        Sakura.instance.getEventBus().unregister(this);
        this.onDisable();
    }

    @Override
    public void toggle() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

	public String getName() {
		return name;
	}

	public T getParent() {
		return parent;
	}

	public List<Value<?>> getValues() {
		return values;
	}
}