package cc.unknown.module.impl.visual;

import java.awt.Color;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.ColorValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;

@ModuleInfo(aliases = {
		"Ambience" }, description = "Allows you to change the time and weather", category = Category.VISUALS)
public final class Ambience extends Module {

	private final NumberValue time = new NumberValue("Time", this, 0, 0, 22999, 1);
	private final NumberValue speed = new NumberValue("Time Speed", this, 0, 0, 20, 1);

	private final ModeValue weather = new ModeValue("Weather", this) {
		{
			add(new SubMode("Unchanged"));
			add(new SubMode("Clear"));
			add(new SubMode("Rain"));
			add(new SubMode("Heavy Snow"));
			add(new SubMode("Light Snow"));
			add(new SubMode("Nether Particles"));
			setDefault("Unchanged");
		}
	};

	public final ColorValue snowColor = new ColorValue("Snow Color", this, Color.WHITE,
			() -> !weather.getValue().getName().equals("Heavy Snow")
					&& !weather.getValue().getName().equals("Light Snow"));

	@Override
	public void onDisable() {
		mc.world.setRainStrength(0);
		mc.world.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
		mc.world.getWorldInfo().setRainTime(0);
		mc.world.getWorldInfo().setThunderTime(0);
		mc.world.getWorldInfo().setRaining(false);
		mc.world.getWorldInfo().setThundering(false);
	}

	@EventLink
	public final Listener<Render3DEvent> onRender3D = event -> {
		mc.world.setWorldTime(
				(time.getValue().intValue() + (System.currentTimeMillis() * speed.getValue().intValue())));
	};

	@EventLink
	public final Listener<MotionEvent> onPreMotionEvent = event -> {
		if (event.isPre()) {
			if (mc.player.ticksExisted % 20 == 0) {

				switch (this.weather.getValue().getName()) {
				case "Clear": {
					mc.world.setRainStrength(0);
					mc.world.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
					mc.world.getWorldInfo().setRainTime(0);
					mc.world.getWorldInfo().setThunderTime(0);
					mc.world.getWorldInfo().setRaining(false);
					mc.world.getWorldInfo().setThundering(false);
					break;
				}
				case "Nether Particles":
				case "Light Snow":
				case "Heavy Snow":
				case "Rain": {
					mc.world.setRainStrength(1);
					mc.world.getWorldInfo().setCleanWeatherTime(0);
					mc.world.getWorldInfo().setRainTime(Integer.MAX_VALUE);
					mc.world.getWorldInfo().setThunderTime(Integer.MAX_VALUE);
					mc.world.getWorldInfo().setRaining(true);
					mc.world.getWorldInfo().setThundering(false);
				}
				}
			}
		}
	};

	@EventLink
	public final Listener<PacketEvent> onPacketReceiveEvent = event -> {
		if (!event.isReceive()) return;
		if (event.getPacket() instanceof S03PacketTimeUpdate) {
			event.setCancelled();
		} else if (event.getPacket() instanceof S2BPacketChangeGameState
				&& !this.weather.getValue().getName().equals("Unchanged")) {
			S2BPacketChangeGameState s2b = (S2BPacketChangeGameState) event.getPacket();

			if (s2b.getGameState() == 1 || s2b.getGameState() == 2) {
				event.setCancelled();
			}
		}
	};

	public float getFloatTemperature(BlockPos blockPos, BiomeGenBase biomeGenBase) {
		if (this.isEnabled()) {
			switch (this.weather.getValue().getName()) {
			case "Nether Particles":
			case "Light Snow":
			case "Heavy Snow":
				return 0.1F;
			case "Rain":
				return 0.2F;
			}
		}

		return biomeGenBase.getFloatTemperature(blockPos);
	}

	public boolean skipRainParticles() {
		final String name = this.weather.getValue().getName();
		return this.isEnabled() && name.equals("Light Snow") || name.equals("Heavy Snow")
				|| name.equals("Nether Particles");
	}

	public NumberValue getTime() {
		return time;
	}

	public NumberValue getSpeed() {
		return speed;
	}

	public ModeValue getWeather() {
		return weather;
	}

	public ColorValue getSnowColor() {
		return snowColor;
	}
}