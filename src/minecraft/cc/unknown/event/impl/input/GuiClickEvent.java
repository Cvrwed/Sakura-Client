package cc.unknown.event.impl.input;

import cc.unknown.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class GuiClickEvent extends CancellableEvent {
    private final int mouseX, mouseY, mouseButton;
}