package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;

@AllArgsConstructor
@Getter
@Setter
public final class ModelVisibilityEvent implements Event {
    private final ItemStack itemStack;
    private int heldItemRight;
}
