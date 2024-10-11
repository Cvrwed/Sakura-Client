package cc.unknown.event.impl.render;

import cc.unknown.event.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

@AllArgsConstructor
@Getter
@Setter
public final class RenderItemEvent extends CancellableEvent {
    private EnumAction enumAction;
    private boolean useItem;
    private float animationProgression, partialTicks, swingProgress;
    private ItemStack itemToRender;
}
