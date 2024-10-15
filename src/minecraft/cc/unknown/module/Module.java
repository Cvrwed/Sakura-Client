package cc.unknown.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import cc.unknown.Sakura;
import cc.unknown.bindable.Bindable;
import cc.unknown.event.impl.other.ModuleToggleEvent;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.module.impl.visual.HUD;
import cc.unknown.util.Accessor;
import cc.unknown.util.interfaces.ThreadAccess;
import cc.unknown.util.interfaces.Toggleable;
import cc.unknown.value.Value;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Module implements Accessor, ThreadAccess, Toggleable, Bindable {

    private String[] aliases;
    private String[] displayName;
    private final List<Value<?>> values = new ArrayList<>();
    private ModuleInfo moduleInfo;
    private boolean enabled;
    private int key;

    public Module() {
        if (this.getClass().isAnnotationPresent(ModuleInfo.class)) {
            this.moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
            this.aliases = Arrays.stream(this.moduleInfo.aliases()).toArray(String[]::new);
            this.key = getModuleInfo().keyBind();
        } else {
            throw new RuntimeException("ModuleInfo annotation not found on " + this.getClass().getSimpleName());
        }
    }

    public Module(final ModuleInfo info) {
        this.moduleInfo = info;

        this.displayName = this.moduleInfo.aliases();
        this.aliases = this.moduleInfo.aliases();
        this.key = getModuleInfo().keyBind();
    }

    @Override
    public String getName() {
        return aliases[0];
    }

    @Override
    public void onKey() {
        this.toggle();
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public void toggle() {
        this.setEnabled(!enabled);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }
    
    public void setEnabled(final boolean enabled) {
        if (this.enabled == enabled || (!this.moduleInfo.allowDisable() && !enabled)) {
            return;
        }

        this.enabled = enabled;

        Sakura.instance.getEventBus().handle(new ModuleToggleEvent(this));

        if (enabled) {
            superEnable();
        } else {
            superDisable();
        }
    }
    
    public final void superEnable() {
        Sakura.instance.getEventBus().register(this);

        this.values.stream()
                .filter(value -> value instanceof ModeValue)
                .forEach(value -> ((ModeValue) value).getValue().register());

        this.values.stream()
                .filter(value -> value instanceof BooleanValue)
                .forEach(value -> {
                    final BooleanValue booleanValue = (BooleanValue) value;
                    if (booleanValue.getMode() != null && booleanValue.getValue()) {
                        booleanValue.getMode().register();
                    }
                });

        this.onEnable();
    }

    public final void superDisable() {
        Sakura.instance.getEventBus().unregister(this);

        this.values.stream()
                .filter(value -> value instanceof ModeValue)
                .forEach(value -> ((ModeValue) value).getValue().unregister());

        this.values.stream()
                .filter(value -> value instanceof BooleanValue)
                .forEach(value -> {
                    final BooleanValue booleanValue = (BooleanValue) value;
                    if (booleanValue.getMode() != null) {
                        booleanValue.getMode().unregister();
                    }
                });

        this.onDisable();
    }

    public List<Value<?>> getAllValues() {
        ArrayList<Value<?>> allValues = new ArrayList<>();

        values.forEach(value -> {
            List<Value<?>> subValues = value.getSubValues();

            allValues.add(value);

            if (subValues != null) {
                allValues.addAll(subValues);
            }
        });

        return allValues;
    }

    public boolean shouldDisplay(HUD instance) {
        if (this instanceof ClickGUI) {
            return false;
        }
        
        if (!this.getModuleInfo().allowDisable()) {
            return false;
        }

        String displaySetting = instance.modulesToShow.getValue().getName().toLowerCase();
        
        switch (displaySetting) {
            case "all":
                return true;
            case "exclude render":
                return !this.getModuleInfo().category().equals(Category.VISUALS);
            case "only bound":
                return this.getKey() != Keyboard.KEY_NONE;
            default:
                return true;
        }
    }
}