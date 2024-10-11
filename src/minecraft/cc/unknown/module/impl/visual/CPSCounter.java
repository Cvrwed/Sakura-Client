package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.ClickEvent;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.EvictingList;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.DragValue;

@ModuleInfo(aliases = { "CPS Counter" }, description = "Displays your clicks per second", category = Category.VISUALS)
public final class CPSCounter extends Module {

	private final DragValue position = new DragValue("Position", this, new Vector2d(200, 200));

	private final Vector2f scale = new Vector2f(RenderUtil.GENERIC_SCALE, RenderUtil.GENERIC_SCALE);
	private final EvictingList<Boolean> clicks = new EvictingList<>(20);
	private boolean clicked;
	private int cps;

	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
		Vector2d position = this.position.position;

		final String titleString = "CPS ";
		final String cpsString = cps + "";

		final float titleWidth = Fonts.MAIN.get(20, Weight.BOLD).width(titleString);
		scale.x = titleWidth + Fonts.MAIN.get(20, Weight.LIGHT).width(cpsString);

		RenderUtil.roundedRectangle(position.x, position.y, scale.x + 6, scale.y - 1, 6,
				getTheme().getBackgroundShade());

		this.position.setScale(new Vector2d(scale.x + 6, scale.y - 1));

		final double textX = position.x + 3.0F;
		final double textY = position.y + scale.y / 2.0F - Fonts.MAIN.get(20, Weight.LIGHT).height() / 4.0F;
		Fonts.MAIN.get(20, Weight.BOLD).drawWithShadow(titleString, textX, textY, getTheme().getFirstColor().getRGB());
		Fonts.MAIN.get(20, Weight.LIGHT).drawWithShadow(cpsString, textX + titleWidth, textY, Color.WHITE.getRGB());
	};

	@EventLink
	public final Listener<ClickEvent> onClick = event -> {
		clicked = true;
	};

	@EventLink
	public final Listener<MotionEvent> onPreMotionEvent = event -> {
		if (event.isPre()) {
			cps = 0;
			clicks.add(clicked);
			clicks.forEach((click) -> {
				if (click) {
					cps++;
				}
			});
			clicked = false;
		}
	};
}