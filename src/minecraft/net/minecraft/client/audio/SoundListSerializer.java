package net.minecraft.client.audio;

import java.lang.reflect.Type;

import org.apache.commons.lang3.Validate;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.util.JsonUtils;

public class SoundListSerializer implements JsonDeserializer<SoundList> {
    public SoundList deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
        final JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "entry");
        final SoundList soundlist = new SoundList();
        soundlist.setReplaceExisting(JsonUtils.getBoolean(jsonobject, "replace", false));
        final SoundCategory soundcategory = SoundCategory.getCategory(JsonUtils.getString(jsonobject, "category", SoundCategory.MASTER.getCategoryName()));
        soundlist.setSoundCategory(soundcategory);
        Validate.notNull(soundcategory, "Invalid category");

        if (jsonobject.has("sounds")) {
            final JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "sounds");

            for (int i = 0; i < jsonarray.size(); ++i) {
                final JsonElement jsonelement = jsonarray.get(i);
                final SoundList.SoundEntry soundlist$soundentry = new SoundList.SoundEntry();

                if (JsonUtils.isString(jsonelement)) {
                    soundlist$soundentry.setSoundEntryName(JsonUtils.getString(jsonelement, "sound"));
                } else {
                    final JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonelement, "sound");
                    soundlist$soundentry.setSoundEntryName(JsonUtils.getString(jsonobject1, "name"));

                    if (jsonobject1.has("type")) {
                        final SoundList.SoundEntry.Type soundlist$soundentry$type = SoundList.SoundEntry.Type.getType(JsonUtils.getString(jsonobject1, "type"));
                        Validate.notNull(soundlist$soundentry$type, "Invalid type");
                        soundlist$soundentry.setSoundEntryType(soundlist$soundentry$type);
                    }

                    if (jsonobject1.has("volume")) {
                        final float f = JsonUtils.getFloat(jsonobject1, "volume");
                        Validate.isTrue(f > 0.0F, "Invalid volume");
                        soundlist$soundentry.setSoundEntryVolume(f);
                    }

                    if (jsonobject1.has("pitch")) {
                        final float f1 = JsonUtils.getFloat(jsonobject1, "pitch");
                        Validate.isTrue(f1 > 0.0F, "Invalid pitch");
                        soundlist$soundentry.setSoundEntryPitch(f1);
                    }

                    if (jsonobject1.has("weight")) {
                        final int j = JsonUtils.getInt(jsonobject1, "weight");
                        Validate.isTrue(j > 0, "Invalid weight");
                        soundlist$soundentry.setSoundEntryWeight(j);
                    }

                    if (jsonobject1.has("stream")) {
                        soundlist$soundentry.setStreaming(JsonUtils.getBoolean(jsonobject1, "stream"));
                    }
                }

                soundlist.getSoundList().add(soundlist$soundentry);
            }
        }

        return soundlist;
    }
}
