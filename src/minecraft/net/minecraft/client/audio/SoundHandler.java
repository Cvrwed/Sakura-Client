package net.minecraft.client.audio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;

public class SoundHandler implements IResourceManagerReloadListener, ITickable {
    private static final Logger logger = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
    private static final ParameterizedType TYPE = new ParameterizedType() {
        public Type[] getActualTypeArguments() {
            return new Type[]{String.class, SoundList.class};
        }

        public Type getRawType() {
            return Map.class;
        }

        public Type getOwnerType() {
            return null;
        }
    };
    public static final SoundPoolEntry missing_sound = new SoundPoolEntry(new ResourceLocation("meta:missing_sound"), 0.0D, 0.0D, false);
    private final SoundRegistry sndRegistry = new SoundRegistry();
    private final SoundManager sndManager;
    private final IResourceManager mcResourceManager;

    public SoundHandler(final IResourceManager manager, final GameSettings gameSettingsIn) {
        this.mcResourceManager = manager;
        this.sndManager = new SoundManager(this, gameSettingsIn);
    }

    public void onResourceManagerReload(final IResourceManager resourceManager) {
        this.sndManager.reloadSoundSystem();
        this.sndRegistry.clearMap();

        for (final String s : resourceManager.getResourceDomains()) {
            try {
                for (final IResource iresource : resourceManager.getAllResources(new ResourceLocation(s, "sounds.json"))) {
                    try {
                        final Map<String, SoundList> map = this.getSoundMap(iresource.getInputStream());

                        for (final Entry<String, SoundList> entry : map.entrySet()) {
                            this.loadSoundResource(new ResourceLocation(s, entry.getKey()), entry.getValue());
                        }
                    } catch (final RuntimeException runtimeexception) {
                        logger.warn("Invalid sounds.json", runtimeexception);
                    }
                }
            } catch (final IOException var11) {
            }
        }
    }

    protected Map<String, SoundList> getSoundMap(final InputStream stream) {
        Map map;

        try {
            map = GSON.fromJson(new InputStreamReader(stream), TYPE);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return map;
    }

    private void loadSoundResource(final ResourceLocation location, final SoundList sounds) {
        final boolean flag = !this.sndRegistry.containsKey(location);
        final SoundEventAccessorComposite soundeventaccessorcomposite;

        if (!flag && !sounds.canReplaceExisting()) {
            soundeventaccessorcomposite = this.sndRegistry.getObject(location);
        } else {
            if (!flag) {
                logger.debug("Replaced sound event location {}", new Object[]{location});
            }

            soundeventaccessorcomposite = new SoundEventAccessorComposite(location, 1.0D, 1.0D, sounds.getSoundCategory());
            this.sndRegistry.registerSound(soundeventaccessorcomposite);
        }

        for (final SoundList.SoundEntry soundlist$soundentry : sounds.getSoundList()) {
            final String s = soundlist$soundentry.getSoundEntryName();
            final ResourceLocation resourcelocation = new ResourceLocation(s);
            final String s1 = s.contains(":") ? resourcelocation.getResourceDomain() : location.getResourceDomain();
            final ISoundEventAccessor<SoundPoolEntry> isoundeventaccessor;

            switch (soundlist$soundentry.getSoundEntryType()) {
                case FILE:
                    final ResourceLocation resourcelocation1 = new ResourceLocation(s1, "sounds/" + resourcelocation.getResourcePath() + ".ogg");
                    InputStream inputstream = null;

                    try {
                        inputstream = this.mcResourceManager.getResource(resourcelocation1).getInputStream();
                    } catch (final FileNotFoundException var18) {
                        logger.warn("File {} does not exist, cannot add it to event {}", new Object[]{resourcelocation1, location});
                        continue;
                    } catch (final IOException ioexception) {
                        logger.warn("Could not load sound file " + resourcelocation1 + ", cannot add it to event " + location, ioexception);
                        continue;
                    } finally {
                        IOUtils.closeQuietly(inputstream);
                    }

                    isoundeventaccessor = new SoundEventAccessor(new SoundPoolEntry(resourcelocation1, soundlist$soundentry.getSoundEntryPitch(), soundlist$soundentry.getSoundEntryVolume(), soundlist$soundentry.isStreaming()), soundlist$soundentry.getSoundEntryWeight());
                    break;

                case SOUND_EVENT:
                    isoundeventaccessor = new ISoundEventAccessor<SoundPoolEntry>() {
                        final ResourceLocation field_148726_a = new ResourceLocation(s1, soundlist$soundentry.getSoundEntryName());

                        public int getWeight() {
                            final SoundEventAccessorComposite soundeventaccessorcomposite1 = SoundHandler.this.sndRegistry.getObject(this.field_148726_a);
                            return soundeventaccessorcomposite1 == null ? 0 : soundeventaccessorcomposite1.getWeight();
                        }

                        public SoundPoolEntry cloneEntry() {
                            final SoundEventAccessorComposite soundeventaccessorcomposite1 = SoundHandler.this.sndRegistry.getObject(this.field_148726_a);
                            return soundeventaccessorcomposite1 == null ? SoundHandler.missing_sound : soundeventaccessorcomposite1.cloneEntry();
                        }
                    };

                    break;
                default:
                    throw new IllegalStateException("IN YOU FACE");
            }

            soundeventaccessorcomposite.addSoundToEventPool(isoundeventaccessor);
        }
    }

    public SoundEventAccessorComposite getSound(final ResourceLocation location) {
        return this.sndRegistry.getObject(location);
    }

    /**
     * Play a sound
     */
    public void playSound(final ISound sound) {
        this.sndManager.playSound(sound);
    }

    /**
     * Plays the sound in n ticks
     */
    public void playDelayedSound(final ISound sound, final int delay) {
        this.sndManager.playDelayedSound(sound, delay);
    }

    public void setListener(final EntityPlayer player, final float p_147691_2_) {
        this.sndManager.setListener(player, p_147691_2_);
    }

    public void pauseSounds() {
        this.sndManager.pauseAllSounds();
    }

    public void stopSounds() {
        this.sndManager.stopAllSounds();
    }

    public void unloadSounds() {
        this.sndManager.unloadSoundSystem();
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update() {
        this.sndManager.updateAllSounds();
    }

    public void resumeSounds() {
        this.sndManager.resumeAllSounds();
    }

    public void setSoundLevel(final SoundCategory category, final float volume) {
        if (category == SoundCategory.MASTER && volume <= 0.0F) {
            this.stopSounds();
        }

        this.sndManager.setSoundCategoryVolume(category, volume);
    }

    public void stopSound(final ISound p_147683_1_) {
        this.sndManager.stopSound(p_147683_1_);
    }

    /**
     * Returns a random sound from one or more categories
     */
    public SoundEventAccessorComposite getRandomSoundFromCategories(final SoundCategory... categories) {
        final List<SoundEventAccessorComposite> list = Lists.newArrayList();

        for (final ResourceLocation resourcelocation : this.sndRegistry.getKeys()) {
            final SoundEventAccessorComposite soundeventaccessorcomposite = this.sndRegistry.getObject(resourcelocation);

            if (ArrayUtils.contains(categories, soundeventaccessorcomposite.getSoundCategory())) {
                list.add(soundeventaccessorcomposite);
            }
        }

        if (list.isEmpty()) {
            return null;
        } else {
            return list.get((new Random()).nextInt(list.size()));
        }
    }

    public boolean isSoundPlaying(final ISound sound) {
        return this.sndManager.isSoundPlaying(sound);
    }
}
