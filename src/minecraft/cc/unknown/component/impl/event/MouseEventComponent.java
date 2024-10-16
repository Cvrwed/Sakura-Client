package cc.unknown.component.impl.event;

import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.MouseInputEvent;
import cc.unknown.event.impl.player.PreMotionEvent;

public class MouseEventComponent extends Component {
	int[] inputs = { 0, 1, 2, 3, 4, 5 };
	boolean[] downs = { false, false, false, false, false, false };

	@EventLink(value = Priority.VERY_LOW)
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		for (int input : inputs) {
			if (Mouse.isButtonDown(input)) {
				if (!downs[input])
					Sakura.instance.getEventBus().handle(new MouseInputEvent(input));
				downs[input] = true;
			} else {
				downs[input] = false;
			}
		}
	};
}
