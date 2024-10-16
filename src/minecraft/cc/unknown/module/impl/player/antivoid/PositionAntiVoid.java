package cc.unknown.module.impl.player.antivoid;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.impl.player.AntiVoid;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.Mode;
import cc.unknown.value.impl.NumberValue;

public class PositionAntiVoid extends Mode<AntiVoid> {

	private final NumberValue distance = new NumberValue("Distance", this, 5, 0, 10, 1);

	public PositionAntiVoid(String name, AntiVoid parent) {
		super(name, parent);
	}

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (mc.player.fallDistance > distance.getValue().floatValue() && !PlayerUtil.isBlockUnder()) {
			event.setPosY(event.getPosY() + mc.player.fallDistance);
		}

	};
}