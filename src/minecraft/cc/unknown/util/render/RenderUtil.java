package cc.unknown.util.render;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.Sakura;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.shader.RiseShaders;
import cc.unknown.util.vector.Vector3d;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;

public final class RenderUtil implements Accessor {

    private static final Frustum FRUSTUM = new Frustum();
    private static final RenderManager RENDER_MANAGER = mc.getRenderManager();
    public static final int GENERIC_SCALE = 22;
    
    /**
     * Better to use gl state manager to avoid bugs
     */
    public static void start() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    /**
     * Better to use gl state manager to avoid bugs
     */
    public static void stop() {
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public static void rectangle(final double x, final double y, final double width, final double height, final Color color) {
        start();

        if (color != null) {
            ColorUtil.glColor(color);
        }

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x, y + height);
        GL11.glEnd();

        stop();
    }

    public void renderAttack(EntityLivingBase target) {
        Sakura.instance.getEventBus().handle(new AttackEvent(target));

        if (!mc.player.isSwingInProgress || mc.player.swingProgressInt >= mc.player.getArmSwingAnimationEnd() / 2 || mc.player.swingProgressInt < 0) {
            mc.player.swingProgressInt = -1;
            mc.player.isSwingInProgress = true;
        }

        if (mc.player.fallDistance > 0) mc.player.onCriticalHit(target);
    }

    public static void rainbowRectangle(final double x, final double y, final double width, final double height) {
        start();

        GL11.glBegin(GL11.GL_QUADS);

        for (double position = x; position <= x + width; position += 0.5) {
            color(Color.getHSBColor((float) ((position - x) / width), 1, 1));

            GL11.glVertex2d(position, y);
            GL11.glVertex2d(position + 0.5f, y);
            GL11.glVertex2d(position + 0.5f, y + height);
            GL11.glVertex2d(position, y + height);
        }

        GL11.glEnd();

        stop();
    }

    public static void rectangle(final double x, final double y, final double width, final double height) {
        rectangle(x, y, width, height, null);
    }

    public void centeredRectangle(final double x, final double y, final double width, final double height, final Color color) {
        rectangle(x - width / 2, y - height / 2, width, height, color);
    }

    public void centeredRectangle(final double x, final double y, final double width, final double height) {
        rectangle(x - width / 2, y - height / 2, width, height, null);
    }

    public static void verticalGradient(final double x, final double y, final double width, final double height, final Color topColor, final Color bottomColor) {
        start();
        GlStateManager.alphaFunc(516, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);

        ColorUtil.glColor(topColor);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);

        ColorUtil.glColor(bottomColor);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x, y + height);

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        stop();
    }
    
    public static void lineNoGl(final double firstX, final double firstY, final double secondX, final double secondY, final Color color) {

        start();
        if (color != null)
            color(color);
        GL11.glLineWidth(2);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_LINES);
        {
        	GL11.glVertex2d(firstX, firstY);
        	GL11.glVertex2d(secondX, secondY);
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public static void begin(final int glMode) {
        GL11.glBegin(glMode);
    }

    public void gradientCentered(double x, double y, final double width, final double height, final Color topColor, final Color bottomColor) {
        x -= width / 2;
        y -= height / 2;
        verticalGradient(x, y, width, height, topColor, bottomColor);
    }

    public static void horizontalGradient(final double x, final double y, final double width, final double height, final Color leftColor, final Color rightColor) {
        start();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);

        ColorUtil.glColor(leftColor);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y + height);

        ColorUtil.glColor(rightColor);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x + width, y);

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        stop();
    }

    public void horizontalCenteredGradient(final double x, final double y, final double width, final double height, final Color leftColor, final Color rightColor) {
        horizontalGradient(x - width / 2, y - height / 2, width, height, leftColor, rightColor);
    }

    public static void image(final ResourceLocation imageLocation, final float x, final float y, final float width, final float height, final Color color) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        color(color);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        mc.getTextureManager().bindTexture(imageLocation);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GlStateManager.resetColor();
        GlStateManager.disableBlend();
    }

    public void image(int textureID, final float x, final float y, final float width, final float height, final Color color) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        color(color);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        mc.getTextureManager().bindTextureById(textureID);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GlStateManager.resetColor();
        GlStateManager.disableBlend();
    }

    public static void image(final ResourceLocation imageLocation, final double x, final double y, final double width, final double height, Color color) {
        image(imageLocation, (float) x, (float) y, (float) width, (float) height, color);
    }

    public static void image(final ResourceLocation imageLocation, final float x, final float y, final float width, final float height) {
        image(imageLocation, x, y, width, height, Color.WHITE);
    }

    public static void image(final ResourceLocation imageLocation, final double x, final double y, final double width, final double height) {
        image(imageLocation, (float) x, (float) y, (float) width, (float) height);
    }

    public static void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public void color(final double red, final double green, final double blue) {
        color(red, green, blue, 1);
    }

    public static void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }

    public void color(Color color, final int alpha) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5);
    }

    public void polygon(final double x, final double y, double sideLength, final double amountOfSides, final boolean filled, final Color color) {
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        if (!filled) GL11.glLineWidth(2);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_STRIP);
        {
            for (double i = 0; i <= amountOfSides / 4; i++) {
                final double angle = i * 4 * (Math.PI * 2) / 360;
                GL11.glVertex2d(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final boolean filled) {
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final Color color) {
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides) {
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final boolean filled, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, filled, color);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final boolean filled) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public void triangle(final double x, final double y, final double sideLength, final boolean filled,
                         final Color color) {
        polygon(x, y, sideLength, 3, filled, color);
    }

    public void triangle(final double x, final double y, final double sideLength, final boolean filled) {
        polygon(x, y, sideLength, 3, filled);
    }

    public void triangle(final double x, final double y, final double sideLength, final Color color) {
        polygon(x, y, sideLength, 3, color);
    }

    public void triangle(final double x, final double y, final double sideLength) {
        polygon(x, y, sideLength, 3);
    }

    public void triangleCentered(double x, double y, final double sideLength, final boolean filled,
                                 final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3, filled, color);
    }

    public void triangleCentered(double x, double y, final double sideLength, final boolean filled) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3, filled);
    }

    public void triangleCentered(double x, double y, final double sideLength, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3, color);
    }

    public void triangleCentered(double x, double y, final double sideLength) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3);
    }

    public static void drawRoundedGradientRect(double x, double y, double width, double height, double radius, Color firstColor, Color secondColor, boolean vertical) {
        RiseShaders.RGQ_SHADER.draw(x, y, width, height, radius, firstColor, secondColor, vertical);
    }
    
    public static void drawRoundedGradientRect(double x, double y, double width, double height, double radius, Color firstColor, Color secondColor, boolean vertical, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom) {
        RiseShaders.RGQ_SHADER.draw(x, y, width, height, radius, firstColor, secondColor, vertical, leftTop, rightTop, rightBottom, leftBottom);
    }

    public static void drawRoundedGradientRectTest(double x, double y, double width, double height, double radius, Color firstColor, Color secondColor, boolean vertical) {
        RiseShaders.RGQ_SHADER_TEST.draw(x, y, width, height, radius, firstColor, secondColor, vertical);
    }

    public static void drawRoundedGradientRectTest(double x, double y, double width, double height, double radius, Color firstColor, Color secondColor, boolean vertical, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom) {
        RiseShaders.RGQ_SHADER_TEST.draw((float) x, (float) y, (float) width, (float) height, (float) radius, firstColor, secondColor, vertical, leftTop, rightTop, rightBottom, leftBottom);
    }

    public static void drawRoundedGradientRectTest(double x, double y, double width, double height, double radius, Color firstColor, Color secondColor, Color thirdColor, boolean vertical, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom) {
        RiseShaders.R_TRI_GQ_SHADER.draw((float) x, (float) y, (float) width, (float) height, (float) radius, firstColor, secondColor, thirdColor, vertical, leftTop, rightTop, rightBottom, leftBottom);
    }

    public static void roundedRectangle(double x, double y, double width, double height, double radius, Color color) {
        RiseShaders.RQ_SHADER.draw((float) x, (float) y, (float) width, (float) height, (float) radius, color);
    }

    public static void roundedRectangle(double x, double y, double width, double height, double radius, Color color, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom) {
        RiseShaders.RQ_SHADER.draw((float) x, (float) y, (float) width, (float) height, (float) radius, color, leftTop, rightTop, rightBottom, leftBottom);
    }

    public static void roundedOutlineRectangle(double x, double y, double width, double height, double radius, double borderSize, Color color) {
        RiseShaders.ROQ_SHADER.draw(x, y, width, height, radius, borderSize, color);
    }

    public static void roundedOutlineGradientRectangle(double x, double y, double width, double height, double radius, double borderSize, Color color1, Color color2) {
        RiseShaders.ROGQ_SHADER.draw(x, y, width, height, radius, borderSize, color1, color2);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void circle(final double x, final double y, final double radius, final Color color) {
        roundedRectangle(x - radius, y - radius, radius * 2, radius * 2, radius, color);
    }

    public static void scissor(double x, double y, double width, double height) {
        final ScaledResolution sr = mc.scaledResolution;
        final double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public static void drawLine(double x, double y, double z, double x1, double y1, double z1, final Color color, final float width) {
        x = x - mc.getRenderManager().renderPosX;
        x1 = x1 - mc.getRenderManager().renderPosX;
        y = y - mc.getRenderManager().renderPosY;
        y1 = y1 - mc.getRenderManager().renderPosY;
        z = z - mc.getRenderManager().renderPosZ;
        z1 = z1 - mc.getRenderManager().renderPosZ;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(width);

        color(color);
        GL11.glBegin(2);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        color(Color.WHITE);
    }

    public static boolean isInViewFrustrum(final Entity entity) {
        return (isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck);
    }

    private static boolean isInViewFrustrum(final AxisAlignedBB bb) {
        final Entity current = mc.getRenderViewEntity();
        FRUSTUM.setPosition(current.posX, current.posY, current.posZ);
        return FRUSTUM.isBoundingBoxInFrustum(bb);
    }

    public static Framebuffer createFrameBuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }

            return new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        }
        return framebuffer;
    }
    
    public static void drawPosESP(double x2, double y2, double z2, float width, final Color color,final Color fillColor) {
        float alpha = 1.0f;

        final float x = (float) (x2 - mc.getRenderManager().renderPosX);
        final float y = (float) (y2 - mc.getRenderManager().renderPosY);
        final float z = (float) (z2 - mc.getRenderManager().renderPosZ);

        float[] rgba = color.getRGBColorComponents(null);
        float[] rgbaFill = fillColor.getRGBColorComponents(null);

        GL11.glLineWidth(2);
        GL11.glColor4f(rgbaFill[0], rgbaFill[1], rgbaFill[2], alpha);
        otherDrawBoundingBox(x, y, z, width, mc.player.height + 0.1f);

        GL11.glLineWidth(2);
        GL11.glColor4f(rgba[0], rgba[1], rgba[2], alpha);
        otherDrawOutlinedBoundingBox(x, y, z, width, mc.player.height + 0.1f);


        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public static void otherDrawOutlinedBoundingBox(float x, float y, float z, double width, double height) {
        width *= 1.5;

        float x1 = (float) (x - width);
        float z1 = (float) (z - width);
        float x2 = (float) (x + width);
        float z2 = (float) (z - width);
        float x3 = (float) (x + width);
        float z3 = (float) (z + width);
        float x4 = (float) (x - width);
        float z4 = (float) (z + width);

        float y2 = (float) (y + height);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)x1, (double)y, (double)z1).endVertex();
        worldrenderer.pos((double)x1, (double)y2, (double)z1).endVertex();
        worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
        worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
        worldrenderer.pos((double)x1, (double)y, (double)z1).endVertex();
        worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
        worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
        worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
        worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
        worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
        worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
        worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
        worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
        worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
        worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
        worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
        worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
        worldrenderer.pos((double)x1, (double)y2, (double)z1).endVertex();
        worldrenderer.pos((double)x1, (double)y, (double)z1).endVertex();
        worldrenderer.endVertex();
        tessellator.draw();
    }

    public static void otherDrawBoundingBox(float x, float y, float z, double width, double height) {
        width *= 1.5;

        float x1 = (float) (x - width);
        float z1 = (float) (z - width);
        float x2 = (float) (x + width);
        float z2 = (float) (z - width);
        float x3 = (float) (x + width);
        float z3 = (float) (z + width);
        float x4 = (float) (x - width);
        float z4 = (float) (z + width);

        float y2 = (float) (y + height);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)x1, (double)y, (double)z1).endVertex();
        worldrenderer.pos((double)x1, (double)y2, (double)z1).endVertex();
        worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
        worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
        worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
        worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
        worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
        worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
        worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
        worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
        worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
        worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
        worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
        worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
        worldrenderer.pos((double)x1, (double)y2, (double)z1).endVertex();
        worldrenderer.pos((double)x1, (double)y, (double)z1).endVertex();
        worldrenderer.pos((double)x1, (double)y, (double)z1).endVertex();
        worldrenderer.pos((double)x2, (double)y, (double)z2).endVertex();
        worldrenderer.pos((double)x3, (double)y, (double)z3).endVertex();
        worldrenderer.pos((double)x4, (double)y, (double)z4).endVertex();
        worldrenderer.pos((double)x1, (double)y2, (double)z1).endVertex();
        worldrenderer.pos((double)x2, (double)y2, (double)z2).endVertex();
        worldrenderer.pos((double)x3, (double)y2, (double)z3).endVertex();
        worldrenderer.pos((double)x4, (double)y2, (double)z4).endVertex();
        worldrenderer.endVertex();
        tessellator.draw();
    }
    
    public static void drawRoundedRect2(final double x, final double y, final double width, final double height, final double radius, final int color) {
        drawRoundedRect(x, y, width - x, height - y, radius, color);
    }
    
    public static void drawRoundedRect(double x, double y, final double width, final double height, final double radius, final int color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double x2 = x + width;
        double y2 = y + height;
        final float f = (color >> 24 & 0xFF) / 255.0f;
        final float f2 = (color >> 16 & 0xFF) / 255.0f;
        final float f3 = (color >> 8 & 0xFF) / 255.0f;
        final float f4 = (color & 0xFF) / 255.0f;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        x *= 2.0;
        y *= 2.0;
        x2 *= 2.0;
        y2 *= 2.0;
        GL11.glDisable(3553);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0) * (radius * -1.0), y + radius + Math.cos(i * Math.PI / 180.0) * (radius * -1.0));
        }
        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0) * (radius * -1.0), y2 - radius + Math.cos(i * Math.PI / 180.0) * (radius * -1.0));
        }
        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x2 - radius + Math.sin(i * Math.PI / 180.0) * radius, y2 - radius + Math.cos(i * Math.PI / 180.0) * radius);
        }
        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x2 - radius + Math.sin(i * Math.PI / 180.0) * radius, y + radius + Math.cos(i * Math.PI / 180.0) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glPopAttrib();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    public static void drawDot(Vec3 pos, double size, int color) {
        double d = size / 2;
        GlStateManager.pushMatrix();
        AxisAlignedBB bbox = new AxisAlignedBB(pos.xCoord - d, pos.yCoord - d, pos.zCoord - d, pos.xCoord + d, pos.yCoord + d, pos.zCoord + d);

        AxisAlignedBB axis = new AxisAlignedBB(bbox.minX - mc.player.posX, bbox.minY - mc.player.posY, bbox.minZ - mc.player.posZ, bbox.maxX - mc.player.posX, bbox.maxY - mc.player.posY, bbox.maxZ - mc.player.posZ);
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(2.0F);
        GL11.glColor4f(r, g, b, a);
        drawBoundingBox(axis, r, g, b, a);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.popMatrix();
    }
    
    public static void drawBoundingBox(AxisAlignedBB abb, float r, float g, float b, float a) {
        Tessellator ts = Tessellator.getInstance();
        WorldRenderer vb = ts.getWorldRenderer();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(abb.minX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.minX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.minZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.maxY, abb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(abb.maxX, abb.minY, abb.maxZ).color(r, g, b, a).endVertex();
        ts.draw();
    }
}
