package cc.unknown.bindable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.KeyboardInputEvent;

public class BindableManager {
    
    public void init() {
        // Has to be a listener to handle the key presses
        Sakura.instance.getEventBus().register(this);
    }

    public List<Bindable> getBinds() {
        List<Bindable> bindableList = new ArrayList<>();

        bindableList.addAll(Sakura.instance.getModuleManager().getAll());
        
        return bindableList;
    }

    @EventLink(value = Priority.VERY_LOW)
    public final Listener<KeyboardInputEvent> onKey = event -> {
        if (event.getGuiScreen() != null || event.isCancelled()) return;

        getBinds().stream()
                .filter(bind -> bind.getKey() == event.getKeyCode())
                .forEach(Bindable::onKey);
    };

    public <T extends Bindable> T get(final String name) {
        // noinspection unchecked
        return (T) getBinds().stream()
                .filter(module -> Arrays.stream(module.getAliases()).anyMatch(alias ->
                        alias.replace(" ", "")
                                .equalsIgnoreCase(name.replace(" ", ""))))
                .findAny().orElse(null);
    }
}
