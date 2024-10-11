package cc.unknown.script.api.wrapper.impl.vector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ScriptVector3d {
    private double x, y, z;

    public void add(ScriptVector3d vector3) {
        this.x += vector3.getX();
        this.y += vector3.getY();
        this.z += vector3.getZ();
    }
}
