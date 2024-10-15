package cc.unknown.module.impl.combat;

import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.RightClickEvent;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.other.AttackEvent;
import cc.unknown.event.impl.render.MouseOverEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.RayCastUtil;
import cc.unknown.util.math.MathUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;

/**
 * @author Alan
 * @since 29/01/2021
 */

@ModuleInfo(aliases = "Reach", description = "Allows you to hit entities from further away", category = Category.COMBAT)
public class Reach extends Module {

	public final BoundsNumberValue range = new BoundsNumberValue("Range", this, 3.0, 4.0, 3.0, 6.0, 0.01);
	private final NumberValue bufferDecrease = new NumberValue("Buffer Decrease", this, 1, 0.1, 10, 0.1, () -> !this.bufferAbuse.getValue());
	private final NumberValue maxBuffer = new NumberValue("Max Buffer", this, 5, 1, 200, 1, () -> !this.bufferAbuse.getValue());
	private final BooleanValue bufferAbuse = new BooleanValue("Buffer Abuse", this, false);

	private int lastId, attackTicks;
	private double combo;

	@EventLink
	public final Listener<MotionEvent> onPreMotionEvent = event -> {
		if (event.isPre()) {
			this.attackTicks++;
		}
	};

	@EventLink
	public final Listener<MouseOverEvent> onMouseOver = event -> {
		event.setRange(
				MathUtil.getRandom(this.range.getValue().doubleValue(), this.range.getSecondValue().doubleValue()));
	};

	@EventLink
	public final Listener<RightClickEvent> onRightClick = event -> mc.objectMouseOver = RayCastUtil
			.rayCast(RotationComponent.rotations, 4.5);

	@EventLink
	public final Listener<AttackEvent> onAttackEvent = event -> {
		final Entity entity = event.getTarget();

		if (this.bufferAbuse.getValue()) {
			if (RayCastUtil.rayCast(RotationComponent.rotations,
					3.0D).typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
				if ((this.attackTicks > 9 || entity.getEntityId() != this.lastId)
						&& this.combo < this.maxBuffer.getValue().intValue()) {
					this.combo++;
				} else {
					event.setCancelled();
				}
			} else {
				this.combo = Math.max(0, this.combo - this.bufferDecrease.getValue().doubleValue());
			}
		} else {
			this.combo = 0;
		}

		this.lastId = entity.getEntityId();
		this.attackTicks = 0;
	};
}