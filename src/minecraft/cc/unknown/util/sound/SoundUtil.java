package cc.unknown.util.sound;

import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import cc.unknown.util.Accessor;

public class SoundUtil implements Accessor {
    private static final HashMap<String, AudioInputStream> sounds = new HashMap<String, AudioInputStream>();
    private static Clip clip;
	
    public static void playSound(final String sound) {
        playSound(sound, 1, 1);
    }

    public static void playSound(final String sound, final float volume, final float pitch) {
        mc.world.playSound(mc.player.posX, mc.player.posY, mc.player.posZ, sound, volume, pitch, false);
    }

    public static void playLocalSound() {
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(SoundUtil.class.getResource("/assets/minecraft/sakura/sound/sakura.wav")));
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void stopLocalSound() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}