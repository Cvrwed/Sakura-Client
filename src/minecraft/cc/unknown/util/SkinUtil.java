package cc.unknown.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import lombok.experimental.UtilityClass;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public class SkinUtil implements Accessor {

    private final Map<String, ResourceLocation> SKIN_CACHE = new HashMap<>();
    private final String NAME_TO_UUID = "https://api.mojang.com/users/profiles/minecraft/";

    public ResourceLocation getResourceLocation(SkinType skinType, String uuid, int size) {
        if (SKIN_CACHE.containsKey(uuid)) return SKIN_CACHE.get(uuid);

        String imageUrl = "http://crafatar.com/avatars/" + uuid;
        ResourceLocation resourceLocation = new ResourceLocation("skins/" + uuid + "?overlay=true");
        ThreadDownloadImageData headTexture = new ThreadDownloadImageData(null, imageUrl, null, null);
        mc.getTextureManager().loadTexture(resourceLocation, headTexture);
        SKIN_CACHE.put(uuid, resourceLocation);
        AbstractClientPlayer.getDownloadImageSkin(resourceLocation, uuid);
        return resourceLocation;
    }

    private String scrape(String url) {
        StringBuilder content = new StringBuilder();
        try {
            final HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Chrome Version 88.0.4324.150");
            connection.connect();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            bufferedReader.close();
        } catch (IOException ignored) {
        }
        return content.toString();
    }

    public String name(String uuid) {
        return null;
    }

    public enum SkinType {
        AVATAR, HELM, BUST, ARMOR_BUST, BODY, ARMOR_BODY, CUBE, SKIN
    }
}
