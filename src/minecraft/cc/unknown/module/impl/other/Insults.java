package cc.unknown.module.impl.other;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.RandomUtils;

import cc.unknown.component.impl.player.BadPacketsComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.StringValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(aliases = "Insults", description = "Insults players after you kill them", category = Category.OTHER)
public final class Insults extends Module {

	public final Map<String, List<String>> map = new HashMap<>();

	private final StringValue prefix = new StringValue("Prefix", this, "");
	private final NumberValue delay = new NumberValue("Delay", this, 0, 0, 50, 1);
	private final BooleanValue randomizer = new BooleanValue("Randomizer", this, false);
	private final BooleanValue autoTell = new BooleanValue("Auto Tell", this, false);

	private final String[] defaultInsults = { "Akemi Client esta en otro level %s", "Config issue %s",
			"Eso te paso por no usar Akemi Client, bobo %s", "Deberias simplemente descargar Akemi Client %s",
			"Esto no se te da, descarga Akemi Client anda. %s",
			"No, no puedo ganar sin hacks, solo con Akemi Client! %s",
			"Antes de insultarme al tell mejor descarga Akemi Client! %s", "He-he-headshot!",
			"Que puedo decir, Akemi Client me da poderes %s", "Akemi Client 1 - %s 0",
			"Eso te pasa por no descargar Akemi Client muajaj! %s", "Deberias descargar akemi cliente yperra %s",
			"Jajaja que malo eres %s", "Puta de yMierda %s", "Du Schweinehund %s", };

	private EntityPlayer target;
	private int ticks;

	@EventLink
	public final Listener<PreMotionEvent> onPreMotion = event -> {
		if (target != null && !mc.world.playerEntities.contains(target)) {
			if (ticks >= delay.getValue().intValue() + Math.random() * 2 && !BadPacketsComponent.bad()) {
				String insult = "";

				insult = defaultInsults[RandomUtils.nextInt(0, defaultInsults.length)];

				insult = String.format(insult, PlayerUtil.name(target));

				if (!this.prefix.getValue().isEmpty()) {
					insult = this.prefix.getValue() + " " + insult;
				}
				final String cmd = "/tell %s ";

				final String generatedString = new Random().ints(97, 123).limit(3)
						.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

				if (randomizer.getValue()) {
					mc.player.sendChatMessage(insult + " " + generatedString);
				}

				if (autoTell.getValue()) {
					mc.player.sendChatMessage(cmd + insult);
				} else {
					mc.player.sendChatMessage(insult);
				}

				target = null;
			}

			ticks++;
		}

	};

	@EventLink
	public final Listener<AttackEvent> onAttack = event -> {
		final Entity target = event.getTarget();

		if (target instanceof EntityPlayer) {
			this.target = (EntityPlayer) target;
			ticks = 0;
		}
	};

	@EventLink
	public final Listener<WorldChangeEvent> onWorldChange = event -> {
		target = null;
		ticks = 0;
	};
}