package cc.unknown.module.impl.world.scaffold.down;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.value.Mode;

public class NormalDownward extends Mode<Scaffold> {

    public NormalDownward(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink(value = Priority.HIGH)
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        if (!Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) return;

        getParent().offset = getParent().offset.add(0,-1,0);
    };
}