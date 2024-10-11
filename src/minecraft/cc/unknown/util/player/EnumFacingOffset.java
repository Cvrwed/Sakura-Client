package cc.unknown.util.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

@Getter
@Setter
@AllArgsConstructor
public class EnumFacingOffset {
    public EnumFacing enumFacing;
    private final Vec3 offset;
}
