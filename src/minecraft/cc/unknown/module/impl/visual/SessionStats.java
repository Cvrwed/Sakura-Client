package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.other.KillEvent;
import cc.unknown.event.impl.other.ServerJoinEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.value.impl.DragValue;
import lombok.AllArgsConstructor;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.StringUtils;

@ModuleInfo(aliases = "Session Stats", description = "Displays a GUI with the stats of your current session", category = Category.VISUALS)
public final class SessionStats extends Module {
	private final DragValue position = new DragValue("", this, new Vector2d(100, 200), true);

	private List<String> killsWords = new ArrayList<>(
			Arrays.asList("no resistió los ataques", "fue brutalmente asesinado", "ha sido asesinado",
					"fue empujado al vacío", "ha sido asesinado", "se cayó al vacío",
					"pensó que era un buen momento de morir a manos", "se ha desconectado intentando correr"));
	private Session session = new Session(0, 0);
	private String time = "0 seconds";

	@EventLink
	public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
		if (mc.player.ticksExisted % 20 == 0) {
			long elapsed = System.currentTimeMillis() - this.session.startTime;
			long hours = TimeUnit.MILLISECONDS.toHours(elapsed);
			long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsed) % 60;
			long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsed) % 60;

			String base = "";
			if (hours > 0)
				base += hours + " " + (hours == 1 ? "hour" : "hours") + ((minutes == 0 ? "" : " "));
			if (minutes > 0)
				base += minutes + " " + (minutes == 1 ? "minute" : "minutes") + (seconds == 0 || hours > 0 ? "" : " ");
			if (seconds > 0 && hours == 0)
				base += seconds + " " + (seconds == 1 ? "second" : "seconds");

			this.time = base;
		}

	};

	@EventLink
	public final Listener<Render2DEvent> onRender2D = event -> {
		if (isClickGui())
			return;

		double padding = 8;
		position.scale = new Vector2d(200 - 70, 100 - 45);

		// Draw all the text itself
		RenderUtil.roundedRectangle(position.position.x, position.position.y, position.scale.x, position.scale.y, 11,
				ColorUtil.withAlpha(Color.black, 100));
		Fonts.MAIN.get(24, Weight.LIGHT).drawCentered("Session Stats", position.position.x + position.scale.x / 2f,
				position.position.y + padding, getTheme().getAccentColor().getRGB());
		Fonts.MAIN.get(18, Weight.LIGHT).drawCentered(time, position.position.x + position.scale.x / 2f,
				position.position.y + padding + 19, new Color(255, 255, 255, 200).getRGB());
		Fonts.MAIN.get(18, Weight.LIGHT).drawCentered("Kills:".toLowerCase() + " " + session.kills,
				position.position.x + 35, position.position.y + padding + 32, new Color(255, 255, 255, 200).getRGB());
		Fonts.MAIN.get(18, Weight.LIGHT).drawWithShadow("Deaths:".toLowerCase() + " " + session.deaths,
				position.position.x + 75, position.position.y + padding + 32, new Color(255, 255, 255, 200).getRGB());

	};

	@EventLink
	public final Listener<KillEvent> onKill = event -> {
		this.session.kills++;
	};

	@EventLink
	public final Listener<PacketEvent> onPacketReceiveEvent = event -> {
		if (!event.isReceive())
			return;

		if (event.getPacket() instanceof S45PacketTitle) {
			S45PacketTitle wrapper = (S45PacketTitle) event.getPacket();
			if (wrapper.getMessage() == null)
				return;

			String message = StringUtils.stripControlCodes(wrapper.getMessage().getUnformattedTextForChat());

			if (message.startsWith(mc.player.getName())) {
				if (killsWords.stream().anyMatch(message::contains)) {
					this.session.deaths++;
				}
			}
		}
	};

	@EventLink
	public final Listener<ServerJoinEvent> onServerJoin = event -> {
		this.session = new Session(0, 0);
	};

	@AllArgsConstructor
	private static class Session {
		int kills, deaths;
		final long startTime = System.currentTimeMillis();
	}
}