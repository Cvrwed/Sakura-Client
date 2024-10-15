package cc.unknown.module.impl.visual;

import static cc.unknown.util.animation.Easing.EASE_IN_BACK;
import static cc.unknown.util.animation.Easing.EASE_OUT_ELASTIC;
import static cc.unknown.util.animation.Easing.EASE_OUT_QUINT;
import static cc.unknown.util.animation.Easing.EASE_OUT_SINE;

import java.awt.Color;

import javax.vecmath.Vector4d;

import org.lwjgl.opengl.GL11;

import cc.unknown.component.impl.render.ProjectionComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.other.AttackEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.ui.clickgui.ClickGui;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.font.Font;
import cc.unknown.util.math.MathUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.StencilUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.DragValue;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(aliases = "Target Info", description = "Displays information about the entity you're fighting", category = Category.VISUALS)
public final class TargetInfo extends Module {

	public final DragValue positionValue = new DragValue("Position", this, new Vector2d(200, 200));
	public final BooleanValue followPlayer = new BooleanValue("Follow Player", this, false);

	public Vector2d position = new Vector2d(0, 0);
	public Entity target;
	public double distanceSq;
	public boolean inWorld;
	public StopWatch stopwatch = new StopWatch();

	private final Font productSansMedium = Fonts.MAIN.get(18, Weight.LIGHT);
	private final int EDGE_OFFSET = 6;
	private final int PADDING = 7;
	private final int INDENT = 4;

	private final Animation openingAnimation = new Animation(EASE_OUT_ELASTIC, 500);
	private final Animation healthAnimation = new Animation(EASE_OUT_SINE, 500);

	@EventLink
	public final Listener<MotionEvent> onPreMotionEvent = event -> {
		if (event.isPre()) {
			if (mc.currentScreen instanceof GuiChat) {
				stopwatch.reset();
				target = mc.player;
			}

			if (target == null) {
				inWorld = false;
				return;
			}

			distanceSq = mc.player.getDistanceSqToEntity(target);
			inWorld = mc.world.loadedEntityList.contains(target);
		}
	};

	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		if (event.getTarget() instanceof AbstractClientPlayer) {
			target = event.getTarget();
			stopwatch.reset();
		}
	};

	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
		if (isClickGui()) return;
		if (target == null) {
			return;
		}

		if (this.followPlayer.getValue() && target != mc.player) {
			Vector4d position = ProjectionComponent.get(target);

			if (position == null)
				return;

			this.position.x = position.z;
			this.position.y = position.w - (position.w - position.y) / 2 - this.positionValue.scale.y / 2f;
		} else {
			this.position = positionValue.position;
		}

		Entity target = this.target;
		if (target == null)
			return;

		boolean out = (!this.inWorld || this.stopwatch.finished(1000));
		openingAnimation.setDuration(out ? 400 : 850);
		openingAnimation.setEasing(out ? EASE_IN_BACK : EASE_OUT_ELASTIC);
		openingAnimation.run(out ? 0 : 1);

		if (openingAnimation.getValue() <= 0)
			return;

		String name = target.getName();

		double x = this.position.x;
		double y = this.position.y;

		double nameWidth = productSansMedium.width(name);
		double health = Math.min(!this.inWorld ? 0 : MathUtil.round(((AbstractClientPlayer) target).getHealth(), 1),
				((AbstractClientPlayer) target).getMaxHealth());
		double healthBarWidth = Math.max(nameWidth + 15, 70);

		healthAnimation.run((health / ((AbstractClientPlayer) target).getMaxHealth()) * healthBarWidth);
		healthAnimation.setEasing(EASE_OUT_QUINT);
		healthAnimation.setDuration(250);
		double healthRemainingWidth = healthAnimation.getValue();

		double hurtTime = (((AbstractClientPlayer) target).hurtTime == 0 ? 0
				: ((AbstractClientPlayer) target).hurtTime - mc.timer.renderPartialTicks) * 0.5;
		int faceScale = 30;
		double faceOffset = hurtTime / 2f;
		double width = EDGE_OFFSET + faceScale + EDGE_OFFSET + healthBarWidth + INDENT + EDGE_OFFSET;
		double height = faceScale + EDGE_OFFSET * 2;
		this.positionValue.setScale(new Vector2d(width, height));

		double scale = openingAnimation.getValue();

		GlStateManager.pushMatrix();
		GlStateManager.translate((x + width / 2) * (1 - scale), (y + height / 2) * (1 - scale), 0);
		GlStateManager.scale(scale, scale, 0);

		// Draw background
		Color background1 = getTheme().getBackgroundShade();
		Color background2 = getTheme().getBackgroundShade();
		Color accent1 = getTheme().getFirstColor();
		Color accent2 = getTheme().getSecondColor();

		RenderUtil.roundedRectangle(x, y, width - 3.5, height - 5, 8, background1);
		RenderUtil.roundedOutlineGradientRectangle(x, y, width - 3.5, height - 5, 8, 0.5,
				ColorUtil.withAlpha(getTheme().getFirstColor(), 200),
				ColorUtil.withAlpha(getTheme().getSecondColor(), 200));
		// Render name

		GlStateManager.pushMatrix();

		productSansMedium.drawWithShadow(name, x + EDGE_OFFSET + faceScale + PADDING - 2.5,
				y + EDGE_OFFSET + INDENT + 1, accent1.hashCode());
		productSansMedium.drawCentered(String.valueOf(Math.round(((AbstractClientPlayer) target).getHealth())),
				x + faceScale + healthBarWidth + 4.5, y + EDGE_OFFSET + INDENT + 1, accent2.hashCode());
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate((x + width / 2) * (1 - scale), (y + height / 2) * (1 - scale), 0);
		GlStateManager.scale(scale, scale, 0);

		// Health background
		RenderUtil.drawRoundedGradientRect(x + EDGE_OFFSET + faceScale + PADDING - 2.5,
				y + EDGE_OFFSET + faceScale - INDENT - 10, healthBarWidth, 6, 3,
				ColorUtil.withAlpha(getTheme().getBackgroundShade(),
						(int) (getTheme().getBackgroundShade().getAlpha() / 1.7f)),
				getTheme().getBackgroundShade(), true);

		// Health
		RenderUtil.drawRoundedGradientRect(x + EDGE_OFFSET + faceScale + PADDING - 2.5,
				y + EDGE_OFFSET + faceScale - INDENT - 10, healthRemainingWidth, 6, 3, accent2, accent1, false);

		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate((x + width / 2) * (1 - scale), (y + height / 2) * (1 - scale), 0);
		GlStateManager.scale(scale, scale, 0);

		// Targets face
		RenderUtil.color(ColorUtil.mixColors(Color.RED, Color.WHITE, hurtTime / 9));

		renderTargetHead((AbstractClientPlayer) target, x + EDGE_OFFSET + faceOffset - 2.5,
				y + EDGE_OFFSET + faceOffset - 2.5, faceScale - hurtTime);

		GlStateManager.popMatrix();

	};

	private void renderTargetHead(final AbstractClientPlayer abstractClientPlayer, final double x, final double y,
			final double size) {
		StencilUtil.initStencil();
		StencilUtil.bindWriteStencilBuffer();
		RenderUtil.roundedRectangle(x, y, size, size, this.getTheme().getRound() * 2,
				this.getTheme().getBackgroundShade());
		StencilUtil.bindReadStencilBuffer(1);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		GlStateManager.enableTexture2D();

		final ResourceLocation resourceLocation = inWorld && abstractClientPlayer.getHealth() > 0
				? abstractClientPlayer.getLocationSkin()
				: RenderSkeleton.getEntityTexture();

		mc.getTextureManager().bindTexture(resourceLocation);

		Gui.drawScaledCustomSizeModalRect(x, y, 4, 4, 4, 4, size, size, 32, 32);
		GlStateManager.disableBlend();
		StencilUtil.uninitStencilBuffer();

		float expand = 0.5f;
		RenderUtil.roundedOutlineRectangle(x - expand, y - expand, size + expand * 2, size + expand * 2,
				this.getTheme().getRound() * 2, 0.5, ColorUtil.withAlpha(Color.BLACK, 40));

	}
}