package cc.unknown.component.impl.render;

import java.awt.Color;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.util.EvictingList;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.animation.Easing;
import cc.unknown.util.font.Font;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.util.tuples.Triple;
import cc.unknown.util.vector.Vector2d;
import net.minecraft.client.renderer.GlStateManager;

public class NotificationComponent extends Component {

	private static final EvictingList<Triple<String, String, Integer>> queue = new EvictingList<>(5);
	private static final StopWatch time = new StopWatch();
	private static Triple<String, String, Integer> current;
	private static final Animation animation = new Animation(Easing.EASE_OUT_EXPO, 900);
	private static final Vector2d SCALE = new Vector2d(140, 30);
	private static final Vector2d ICON_SCALE = new Vector2d(20, 20);
	private static final Vector2d POSITION = new Vector2d(5, 27);
	private static final double SPACER = (SCALE.y - ICON_SCALE.y) / 2f;

	private static final Font bold = Fonts.MAIN.get(15, Weight.BOLD);
	private static final Font light = Fonts.MAIN.get(15, Weight.LIGHT);

	@EventLink(value = Priority.VERY_HIGH)
	public final Listener<Render2DEvent> onRender2DEvent = event -> {
		if (current == null)
			return;

		boolean out = time.finished(current.getThird());

		animation.run(out ? 1.1 : 1);
		animation.setDuration(500);
		animation.setEasing(Easing.EASE_OUT_EXPO);
		double scale = animation.getValue();
		double opacity = 1 - 10 * Math.abs(1 - animation.getValue());

		if (animation.isFinished() && out)
			return;

		GlStateManager.pushMatrix();
		GlStateManager.translate((POSITION.x + SCALE.x / 2) * (1 - scale), (POSITION.y + SCALE.y / 2) * (1 - scale), 0);
		GlStateManager.scale(scale, scale, 0);

		RenderUtil.roundedRectangle(POSITION.x, POSITION.y, SCALE.x, SCALE.y, 10, ColorUtil.withAlpha(
				getTheme().getBackgroundShade(), (int) (getTheme().getBackgroundShade().getAlpha() * opacity)));

		RenderUtil.roundedRectangle(POSITION.x + SPACER, POSITION.y + SPACER, ICON_SCALE.x, ICON_SCALE.y, 6,
				ColorUtil.withAlpha(Color.WHITE, (int) (255 * opacity)));

		bold.drawWithShadow(current.getFirst(), POSITION.x + SPACER + ICON_SCALE.x + SPACER, POSITION.y + SPACER + 3,
				ColorUtil.withAlpha(getTheme().getFirstColor(), (int) (255 * opacity)).getRGB());

		light.drawWithShadow(current.getSecond(), POSITION.x + SPACER + ICON_SCALE.x + SPACER,
				POSITION.y + SPACER + 0.5 + SPACER * 0.7 + bold.height(),
				ColorUtil.withAlpha(Color.WHITE, (int) (255 * opacity)).getRGB());

		GlStateManager.popMatrix();

	};

	@EventLink(value = Priority.VERY_HIGH)
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		if (mc.player.ticksExisted % 5 != 0)
			return;

		if (!queue.isEmpty() && (current == null || time.finished(current.getThird() + 200))) {
			if (current != null)
				queue.remove(current);

			if (!queue.isEmpty()) {
				current = queue.get(0);
				time.reset();
			}
			SCALE.x = Math.max(140, light.width(current.getSecond()) + SPACER * 3 + ICON_SCALE.x + 2);
		}

	};

	public static void post(String title, String description) {
		post(title, description, 3000);
	}

	public static void post(String title, String description, Integer time) {
		queue.add(new Triple<>(title, description, time));
	}

}
