package cc.unknown.module.impl.other;

import cc.unknown.component.impl.render.NotificationComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.sound.MusicUtil;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.StringValue;
import cc.unknown.value.impl.SubMode;

@ModuleInfo(aliases = "Music Player", description = "Shows notification on your currently playing track", category = Category.OTHER)
public class MusicPlayer extends Module {

    private final ModeValue mode = new ModeValue("Type", this)
            .add(new SubMode("I Love Radio"))
            .add(new SubMode("NCS"))
            .add(new SubMode("NightCore"))
            .add(new SubMode("90s"))
            .add(new SubMode("Local"))
            .setDefault("Local");

    private final StringValue text = new StringValue("URL", this, "C:\\Users\\admin\\Music\\", () -> !mode.is("Local"));
    
    private boolean started = false;

    @EventLink(value = Priority.EXTREMELY_HIGH)
    public final Listener<TickEvent> onTick = event -> {
        if (started) {
            if (mode.is("Local")) {
                MusicUtil.loadMusicFiles(text.getValue());
                if (MusicUtil.getMusicFiles() != null && !MusicUtil.getMusicFiles().isEmpty()) {
                    MusicUtil.playSong();
                } else {
                    NotificationComponent.post("Music Player", "No local music files found.", 1000);
                }
            } else {
                NotificationComponent.post("Music Player", "Playing " + mode.getValue().getName() + " playlist", 3000);
                MusicUtil.playOtherMusic(mode.getValue().getName());
            }
            started = false;
        }
    };
    
    @Override
    public void onEnable() {
    	started = true;
    }

    @Override
    public void onDisable() {   
        MusicUtil.stopMusic();
        MusicUtil.stopLocal();
        started = false;
    }
}