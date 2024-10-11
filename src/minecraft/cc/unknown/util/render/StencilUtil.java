package cc.unknown.util.render;

import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glStencilOp;

import org.lwjgl.opengl.GL30;

import cc.unknown.util.Accessor;
import net.minecraft.client.shader.Framebuffer;

/**
 * @author Patrick
 * @since 11/16/2021
 */
public class StencilUtil implements Accessor {

    private static void recreate(final Framebuffer framebuffer) {
        GL30.glDeleteRenderbuffers(framebuffer.depthBuffer);
        final int depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_STENCIL, mc.displayWidth, mc.displayHeight);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
    }

    public static void checkSetupFBO(final Framebuffer framebuffer) {
        if (framebuffer != null) {
            if (framebuffer.depthBuffer > -1) {
                recreate(framebuffer);
                framebuffer.depthBuffer = -1;
            }
        }
    }

    public static void initStencil() {
        initStencil(mc.getFramebuffer());
    }

    public static void initStencil(final Framebuffer framebuffer) {
        framebuffer.bindFramebuffer(false);
        checkSetupFBO(framebuffer);
        glClear(GL_STENCIL_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);
    }

    public static void bindWriteStencilBuffer() {
        glStencilFunc(GL_ALWAYS, 1, 1);
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        glColorMask(false, false, false, false);
    }

    public static void bindReadStencilBuffer(final int ref) {
        glColorMask(true, true, true, true);
        glStencilFunc(GL_EQUAL, ref, 1);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
    }

    public static void uninitStencilBuffer() {
        glDisable(GL_STENCIL_TEST);
    }
}