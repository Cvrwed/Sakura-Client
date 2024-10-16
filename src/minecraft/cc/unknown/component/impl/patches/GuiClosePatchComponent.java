package cc.unknown.component.impl.patches;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreMotionEvent;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class GuiClosePatchComponent extends Component {

	private boolean inGUI;

	@EventLink
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		if (mc.currentScreen == null && inGUI) {
			for (final KeyBinding bind : mc.gameSettings.keyBindings) {
				bind.setPressed(GameSettings.isKeyDown(bind));
			}
		}

		inGUI = mc.currentScreen != null;

	};
}
