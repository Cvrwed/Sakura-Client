package cc.unknown.util.shader.impl;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.util.shader.base.RiseShaderProgram;
import cc.unknown.util.shader.base.ShaderUniforms;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public final class RGQTestShader {
    private final RiseShaderProgram program = new RiseShaderProgram("rgqtest.glsl", "vertex.vsh");

    public void draw(float x, float y, float width, float height, float radius, Color firstColor, Color secondColor, boolean vertical, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom) {
        int programId = this.program.getProgramId();
        this.program.start();

        ShaderUniforms.uniform2f(programId, "u_size", width, height);
        ShaderUniforms.uniform1f(programId, "u_radius", radius);
        ShaderUniforms.uniform4f(programId, "u_first_color", firstColor.getRed() / 255.0F, firstColor.getGreen() / 255.0F, firstColor.getBlue() / 255.0F, firstColor.getAlpha() / 255.0F);
        ShaderUniforms.uniform4f(programId, "u_second_color", secondColor.getRed() / 255.0F, secondColor.getGreen() / 255.0F, secondColor.getBlue() / 255.0F, secondColor.getAlpha() / 255.0F);
        ShaderUniforms.uniform1i(programId, "u_direction", vertical ? 1 : 0);
        ShaderUniforms.uniform1f(programId, "u_time", (System.currentTimeMillis() - Minecraft.getMinecraft().getStartMillisTime()) / 1000F);
        ShaderUniforms.uniform4f(programId, "u_edges", leftTop ? 1.0F : 0.0F, rightTop ? 1.0F : 0.0F, rightBottom ? 1.0F : 0.0F, leftBottom ? 1.0F : 0.0F);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RiseShaderProgram.drawQuad(x, y, width, height);
        GlStateManager.disableBlend();
        RiseShaderProgram.stop();
    }

    public void draw(double x, double y, double width, double height, double radius, Color firstColor, Color secondColor, boolean vertical) {
        draw((float) x, (float) y, (float) width, (float) height, (float) radius, firstColor, secondColor, vertical, true, true, true, true);
    }
}
