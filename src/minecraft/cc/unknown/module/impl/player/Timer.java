package cc.unknown.module.impl.player;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.math.MathUtil;
import cc.unknown.value.impl.BoundsNumberValue;

@ModuleInfo(aliases = "Timer", description = "Changes the speed that Minecraft runs at", category = Category.PLAYER)
public final class Timer extends Module {

	private final BoundsNumberValue timer = new BoundsNumberValue("Timer", this, 1, 2, 0.1, 10, 0.05);

	@EventLink(value = Priority.MEDIUM)
	public final Listener<MotionEvent> onPreMotion = event -> {
		if (event.isPre()) {
			mc.timer.timerSpeed = (float) MathUtil.getRandom(timer.getValue().floatValue(), timer.getSecondValue().floatValue());
		}
	};

	@Override
	public void onDisable() {
		if (this.mc.timer.timerSpeed != 1) {
			this.mc.timer.timerSpeed = 1;
		}
	}

}
