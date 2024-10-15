package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.PostStrafeEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.ui.clickgui.ClickGui;
import cc.unknown.util.math.MathUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.util.vector.Vector3d;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.DragValue;

@ModuleInfo(aliases = {"BPS Counter"}, description = "Displays your current speed", category = Category.VISUALS)
public final class BPSCounter extends Module {

    private final DragValue position = new DragValue("Position", this, new Vector2d(200, 200));

    private final Vector2f scale = new Vector2f(RenderUtil.GENERIC_SCALE, RenderUtil.GENERIC_SCALE);
    private Vector3d positionVector = new Vector3d(0, 0, 0);
    private String speed = "";

    @EventLink
    public final Listener<PostStrafeEvent> onPostStrafe = event -> {
        speed = String.valueOf(MathUtil.round(new Vector3d(mc.player.posX, 0, mc.player.posZ).distance(positionVector) * 20 * mc.timer.timerSpeed, 2));
        positionVector = new Vector3d(mc.player.posX, 0, mc.player.posZ);
    };

    @EventLink
    public final Listener<Render2DEvent> onRender2D = event -> {
		if (isClickGui()) return;

    	Vector2d position = this.position.position;
		
        final String titleString = "BPS ";
        final String bpsString = speed;

        final float titleWidth = Fonts.MAIN.get(20, Weight.BOLD).width(titleString);
        scale.x = titleWidth + Fonts.MAIN.get(20, Weight.LIGHT).width(bpsString);

        RenderUtil.roundedRectangle(position.x, position.y, scale.x + 6, scale.y - 1, 6, getTheme().getBackgroundShade());

        this.position.setScale(new Vector2d(scale.x + 6, scale.y - 1));

        final double textX = position.x + 3.0F;
        final double textY = position.y + scale.y / 2.0F - Fonts.MAIN.get(20, Weight.LIGHT).height() / 4.0F;

        Fonts.MAIN.get(20, Weight.BOLD).drawWithShadow(titleString, textX, textY, getTheme().getFirstColor().getRGB());
        Fonts.MAIN.get(20, Weight.LIGHT).drawWithShadow(bpsString, textX + titleWidth, textY, Color.WHITE.getRGB());
    };
}