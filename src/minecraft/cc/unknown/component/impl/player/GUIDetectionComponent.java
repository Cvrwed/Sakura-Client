package cc.unknown.component.impl.player;

import cc.unknown.component.impl.Component;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.EntityList;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public final class GUIDetectionComponent extends Component {

	private static boolean userInterface;

	@EventLink(value = Priority.VERY_HIGH)
	public final Listener<MotionEvent> onPreMotionEvent = event -> {
		if (event.isPre()) {
			userInterface = false;

			if (mc.currentScreen instanceof GuiChest) {
				final Container container = mc.player.openContainer;

				int confidence = 0, totalSlots = 0, amount = 0;

				for (final Slot slot : container.inventorySlots) {
					if (slot.getHasStack() && amount++ <= 26 /* Amount of slots in a chest */) {
						final ItemStack itemStack = slot.getStack();

						if (itemStack == null) {
							continue;
						}

						final String name = itemStack.getDisplayName();
						final String expectedName = expectedName(itemStack);
						final String strippedName = name.toLowerCase().replace(" ", "");
						final String strippedExpectedName = expectedName.toLowerCase().replace(" ", "");

						if (strippedName.contains(strippedExpectedName)) {
							confidence -= 0.1;
						} else {
							confidence++;
						}

						totalSlots++;
					}
				}

				userInterface = (float) confidence / (float) totalSlots > 0.5f;
			}
		}
	};

	public static boolean inGUI() {
		return userInterface;
	}

	private String expectedName(final ItemStack stack) {
		String s = ("" + StatCollector.translateToLocal(stack.getUnlocalizedName() + ".name")).trim();
		final String s1 = EntityList.getStringFromID(stack.getMetadata());

		if (s1 != null) {
			s = s + " " + StatCollector.translateToLocal("entity." + s1 + ".name");
		}

		return s;
	}
}
