package cc.unknown.module.impl.other;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.tuples.Doble;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(aliases = "Anti AFK", description = "Prevents you from getting AFK kicked by servers", category = Category.OTHER)
public final class AntiAFK extends Module {

	private int lastInput;

	@EventLink
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		GameSettings gameSettings = mc.gameSettings;

		List<Doble<BooleanSupplier, Runnable>> keyActions = new ArrayList<>();
		keyActions.add(new Doble<>(gameSettings.keyBindJump::isPressed, () -> lastInput = 0));
		keyActions.add(new Doble<>(gameSettings.keyBindRight::isPressed, () -> lastInput = 0));
		keyActions.add(new Doble<>(gameSettings.keyBindForward::isPressed, () -> lastInput = 0));
		keyActions.add(new Doble<>(gameSettings.keyBindLeft::isPressed, () -> lastInput = 0));
		keyActions.add(new Doble<>(gameSettings.keyBindBack::isPressed, () -> lastInput = 0));

		keyActions.forEach(pair -> {
			if (pair.getFirst().getAsBoolean()) {
				pair.getSecond().run();
			}
		});

		lastInput++;

		if (lastInput < 20 * 10)
			return;

		int ticksExisted = mc.player.ticksExisted;

		Runnable toggleMovementKeys = () -> {
			gameSettings.keyBindRight.setPressed(false);
			gameSettings.keyBindLeft.setPressed(false);
			gameSettings.keyBindJump.setPressed(false);
		};

		if (ticksExisted % 5 == 0) {
			toggleMovementKeys.run();
		}

		if (ticksExisted % 20 == 0) {
			boolean shouldToggleRight = ticksExisted % 40 == 0;
			gameSettings.keyBindRight.setPressed(shouldToggleRight);
			gameSettings.keyBindLeft.setPressed(!shouldToggleRight);
		}

		if (ticksExisted % 100 == 0) {
			gameSettings.keyBindJump.setPressed(true);
		}

	};
}
