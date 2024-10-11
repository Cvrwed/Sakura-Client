package cc.unknown.event.impl.render;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.EntityLivingBase;

@AllArgsConstructor
@Getter
@Setter
public final class RenderModelEvent implements Event {
    private final EntityLivingBase entity;
    private final Runnable modelRenderer, layerRenderer;
}