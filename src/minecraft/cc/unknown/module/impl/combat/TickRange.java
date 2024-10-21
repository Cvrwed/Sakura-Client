package cc.unknown.module.impl.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.FriendAndTargetComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.util.tuples.Doble;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = "Tick Range", description = "Freezes minecraft instance to get closer to your target", category = Category.COMBAT)
public class TickRange extends Module {

	private final NumberValue coolDown = new NumberValue("Delay after dash to be able again to dash", this, 1, 1, 8, 0.5);
	private final NumberValue range2 = new NumberValue("Distance from target to start dashing", this, 3, 3, 6, 0.1);
	private final NumberValue freeze = new NumberValue("Freeze ticks duration on dash", this, 2, 1, 70, 1);
	private final NumberValue packets = new NumberValue("Packets value to send on freeze", this, 2, 1, 70, 1);

	private int durationTicks = 0, waitTicks = 0, delayTicks = 0;
	public static boolean publicFreeze = false;

	@Override
	public void onEnable() {
		clear();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		publicFreeze = false;
		clear();
		super.onDisable();
	}

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		if (!isInGame() || getModule(Scaffold.class).isEnabled()) return;
		publicFreeze = false;

		if (waitTicks == 0) {
			waitTicks--;
			for (int i = 0; i < packets.getValue().intValue() * 2.5; i++) {
				mc.world.tick();
			}
		}
		if (waitTicks > 0) {
			waitTicks--;
			publicFreeze = true;
		} else {
		}
		if (delayTicks > 0) {

			delayTicks--;
		}

		if (getModule(KillAura.class).target != null) {
			double afterRange = RotationUtil.nearestRotation(getModule(KillAura.class).target.getEntityBoundingBox());
			if (afterRange < range2.getValue().floatValue() && afterRange > 3 && mc.gameSettings.keyBindForward.pressed) {
				if (delayTicks > 0) {
				} else {
					waitTicks = (int) (freeze.getValue().intValue() * 2.5);
					delayTicks = (int) (coolDown.getValue().floatValue() * 160);
				}
			}
		} else {
			clear();
			return;
		}
	};

	public void clear() {
		publicFreeze = false;
		durationTicks = 0;
	}

}
