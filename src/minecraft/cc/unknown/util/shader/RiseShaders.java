package cc.unknown.util.shader;

import cc.unknown.util.shader.impl.*;

public interface RiseShaders {

    RQShader RQ_SHADER = new RQShader();
    RGQShader RGQ_SHADER = new RGQShader();
    ROQShader ROQ_SHADER = new ROQShader();
    ROGQShader ROGQ_SHADER = new ROGQShader();
    RGQTestShader RGQ_SHADER_TEST = new RGQTestShader();

    RTriGQShader R_TRI_GQ_SHADER = new RTriGQShader();
}
