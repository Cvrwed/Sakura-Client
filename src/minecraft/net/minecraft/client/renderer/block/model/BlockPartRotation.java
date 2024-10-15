package net.minecraft.client.renderer.block.model;

import org.lwjgl.util.vector.Vector3f;

import lombok.RequiredArgsConstructor;
import net.minecraft.util.EnumFacing;

@RequiredArgsConstructor
public class BlockPartRotation {
    public final Vector3f origin;
    public final EnumFacing.Axis axis;
    public final float angle;
    public final boolean rescale;
}
